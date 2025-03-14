package tk.skyblocksandbox.skyblocksandbox.menu;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import tk.skyblocksandbox.skyblocksandbox.SkyblockSandbox;
import tk.skyblocksandbox.skyblocksandbox.menu.providers.MainSkyblockMenu;
import tk.skyblocksandbox.skyblocksandbox.player.SkyblockPlayer;

import java.util.HashMap;
import java.util.Map;

public final class MenuFactory {

    private final Map<MenuList, SmartInventory> menus = new HashMap<>();

    public MenuFactory() {
        registerMenu(MenuList.SKYBLOCK_MENU_MAIN, buildFromProvider(
                new MainSkyblockMenu(),
                9, 6,
                "menu.skyblock_menu_main",
                "Skyblock Menu"
        ));
    }

    public SmartInventory buildFromProvider(InventoryProvider provider, int length, int width, String menuLocale, String title) {
        SmartInventory.Builder builder = SmartInventory.builder()
                .id(menuLocale)
                .provider(provider)
                .size(width, length)
                .title(title)
                .manager(SkyblockSandbox.getManagement().getInventoryManager());

        return builder.build();
    }

    public void registerMenu(MenuList identifier, SmartInventory inventory) {
        menus.put(identifier, inventory);
    }

    public void serveMenu(SkyblockPlayer sbPlayer, MenuList menuType) {
        SmartInventory menu = menus.getOrDefault(menuType, null);
        menu.open(sbPlayer.getBukkitPlayer());
    }

    public enum MenuList {
        SKYBLOCK_MENU_MAIN,
        SKYBLOCK_MENU_SETTINGS,
        SKYBLOCK_MENU_ITEM_CREATOR
    }

}
