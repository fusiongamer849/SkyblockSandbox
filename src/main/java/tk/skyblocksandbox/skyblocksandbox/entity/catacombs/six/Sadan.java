package tk.skyblocksandbox.skyblocksandbox.entity.catacombs.six;

import org.bukkit.entity.EntityType;
import tk.skyblocksandbox.skyblocksandbox.entity.SkyblockEntity;
import tk.skyblocksandbox.skyblocksandbox.entity.SkyblockEntityData;

public final class Sadan extends SkyblockEntity {

    public Sadan() {
        super(EntityType.GIANT);
    }

    @Override
    public SkyblockEntityData getEntityData() {
        SkyblockEntityData entityData = new SkyblockEntityData();

        entityData.entityName = "Sadan";
        entityData.isBoss = true;

        entityData.canTakeKnockback = true;
        entityData.isUndead = true;

        entityData.health = 40000000;
        entityData.defense = 0;
        entityData.vanillaHealth = 100;

        return entityData;
    }
}
