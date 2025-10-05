package me.solar.apolloLibrary.menus;

import me.solar.apolloLibrary.utils.Common;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class PresetItems {
    private PresetItems() {
    }

    public static MenuItem getCloseButton(JavaPlugin plugin, Material material, String name, MenuClickType clickType, String... lore) {
        MenuItem item = new MenuItem(material, name, lore);
        MenuAction closeAction = (menuEvent) -> Common.runTaskLater(plugin, () -> menuEvent.getPlayer().closeInventory(), 1L);
        switch (clickType) {
            case LEFT_CLICK:
                item.withOnClick(closeAction);
                break;
            case RIGHT_CLICK:
                item.withOnRightClick(closeAction);
                break;
            case SHIFT_LEFT_CLICK:
                item.withOnShiftLeftClick(closeAction);
                break;
            case SHIFT_RIGHT_CLICK:
                item.withOnShiftRightClick(closeAction);
                break;
            case MIDDLE_CLICK:
                item.withOnMiddleClick(closeAction);
                break;
            case ALL:
                item.withOnClick(closeAction);
                item.withOnRightClick(closeAction);
                item.withOnShiftLeftClick(closeAction);
                item.withOnShiftRightClick(closeAction);
                item.withOnMiddleClick(closeAction);
        }

        return item;
    }

    public static MenuItem getCloseButton(JavaPlugin plugin, Material material, String name, String... lore) {
        return getCloseButton(plugin, material, name, MenuClickType.ALL, lore);
    }

    public static MenuItem getCloseButton(JavaPlugin plugin) {
        return getCloseButton(plugin, Material.BARRIER, "Close");
    }
}

