package tk.skyblocksandbox.skyblocksandbox.item.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import tk.skyblocksandbox.skyblocksandbox.SkyblockSandbox;
import tk.skyblocksandbox.skyblocksandbox.entity.SkyblockEntity;
import tk.skyblocksandbox.skyblocksandbox.item.SandboxItem;
import tk.skyblocksandbox.skyblocksandbox.item.SkyblockItemData;
import tk.skyblocksandbox.skyblocksandbox.item.SkyblockItemIds;
import tk.skyblocksandbox.skyblocksandbox.player.SkyblockPlayer;
import tk.skyblocksandbox.skyblocksandbox.util.Calculator;
import tk.skyblocksandbox.skyblocksandbox.util.Lore;
import tk.skyblocksandbox.skyblocksandbox.util.Utility;

import java.util.Collection;

public final class Hyperion extends SandboxItem {

    public Hyperion() {
        super(Material.IRON_SWORD, "Hyperion", SkyblockItemIds.HYPERION);
    }

    @Override
    public Collection<String> getLore() {
        Lore generator = new Lore(16,
                " ",
                Utility.colorize("&7Deals &a+50% &7damage to"),
                Utility.colorize("&7Withers. Grants &c+1 ❁ Damage"),
                Utility.colorize("&7and &a+2&b ✎ Intelligence"),
                Utility.colorize("&7per &cCatacombs &7level."),
                " ",
                Utility.colorize("&7Your Catacombs level: &c0")
        );

        return generator.genericLore(this);
    }

    @Override
    public SkyblockItemData getItemData() {
        SkyblockItemData finalItemData = new SkyblockItemData();

        finalItemData.hasAbility = true;
        finalItemData.abilityTrigger = RIGHT_CLICK_TRIGGER;
        finalItemData.abilityName = "Wither Impact";
        finalItemData.abilityDescription = "&7Teleport &a10 blocks &7ahead of\n" +
                "&7you. Then implode dealing\n" +
                "&c10,000 &7damage to nearby\n" +
                "&7enemies. Also applies the wither\n" +
                "&7shield scroll ability reducing\n&7damage taken and granting an\n" +
                "&7absorption shield for &e5\n" +
                "&7seconds.";

        finalItemData.itemType = SWORD;
        finalItemData.isDungeonItem = true;
        finalItemData.canHaveStars = true;
        finalItemData.rarity = LEGENDARY;

        finalItemData.baseDamage = 260;
        finalItemData.baseStrength = 150;
        finalItemData.baseIntelligence = 350;
        finalItemData.baseFerocity = 30;

        return finalItemData;
    }

    @Override
    public void ability(int action, SkyblockPlayer sbPlayer) {
        if(action != SandboxItem.INTERACT_RIGHT_CLICK) return;

        if(!sbPlayer.manaCheck(300, "Wither Impact")) return;
        Player player = sbPlayer.getBukkitPlayer();
        player.setFallDistance(0);

        // Teleport System - START \\
        if(!sbPlayer.getPlayerData().limitedMovement) {
            int blocks = 10;
            Location playerLocation = player.getLocation(); // get the player's location
            Vector playerDirection = player.getLocation().getDirection(); // get the player's direction
            playerDirection.multiply(blocks);
            Location targetLocation = playerLocation.add(playerDirection); // add the direction to the player's location, now we have the player's target location
            targetLocation.add(0, 1, 0);
            if(player.getTargetBlockExact(10) == null) {
                player.teleport(targetLocation); // teleport the player to the target location
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, targetLocation, 3);
            } else {
                player.sendMessage(Utility.colorize("&cThere are blocks in the way!"));

                Location raycastLocation;
                Location raycastTarget;
                Vector raycastVector;
                for(int i = 1; i <= blocks; i++) {
                    raycastLocation = player.getLocation();
                    raycastVector = player.getLocation().getDirection();

                    raycastVector.multiply(i);
                    raycastTarget = raycastLocation.add(raycastVector);
                    Material block = raycastTarget.getBlock().getType();

                    if(block != Material.AIR) {

                        raycastLocation = player.getLocation();
                        raycastVector = player.getLocation().getDirection();

                        raycastVector.multiply(i - 1);
                        raycastTarget = raycastLocation.add(raycastVector);

                        raycastTarget.add(0, 1, 0);

                        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, raycastTarget, 3);
                        player.teleport(raycastTarget);
                        break;
                    }

                }
            }
        } else {
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 3);
        }
        // Teleport System - END \\

        int damage = 10000 + sbPlayer.getPlayerData().bonusAbilityDamage; // TODO: Calculate ability damage properly.
        int entityCount = 0;
        for(Entity e : player.getNearbyEntities(6, 6, 6)) {
            if(e instanceof Damageable && e != player) {
                Damageable entity = (Damageable) e;
                if(entity instanceof ArmorStand) return;

                if(entity instanceof Player) {
                    Object rawTarget = SkyblockSandbox.getApi().getPlayerManager().isCustomPlayer((Player) entity);
                    if(!(rawTarget instanceof SkyblockPlayer)) return;
                    SkyblockPlayer sbTarget = (SkyblockPlayer) rawTarget;

                    if(sbTarget.getPlayerData().canTakeAbilityDamage) {
                        Calculator.damage(sbTarget, damage, false);
                        entityCount++;
                    }
                }else{
                    SkyblockEntity sbEntity = SkyblockSandbox.getManagement().getEntityManager().getEntity(entity);
                    if(sbEntity == null) return;

                    entityCount++;
                    entity.setLastDamageCause(new EntityDamageByEntityEvent(player, e, EntityDamageEvent.DamageCause.CUSTOM, damage));
                    Calculator.damage(sbEntity, damage, false);
                }

            }
        }

        if(entityCount > 0) {
            player.sendMessage(Utility.colorize("&7Your Implosion did &c" + (Utility.commafy("" + (damage * entityCount))) + "&7 damage to " + entityCount + " enemies."));
        }

        if(sbPlayer.getPlayerData().canUseWitherShield) {
//            sbPlayer.addAbsorptionHealth( sbPlayer.getPlayerData().critDamage * 2 ); // TODO: Implement absorption.
            sbPlayer.getPlayerData().canUseWitherShield = false;

//            Bukkit.getScheduler().runTaskLater(SkyblockSandbox.getInstance(), () -> sbPlayer.removeAbsorption( (sbPlayer.getPlayerData().critDamage * 2) ), 20*5L); // TODO: Implement absorption.
        }
    }
}