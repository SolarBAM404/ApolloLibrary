package me.solar.apolloLibrary.menus;

import lombok.Getter;
import me.solar.apolloLibrary.core.ApolloPlugin;
import me.solar.apolloLibrary.utils.Common;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {

    private static MenuListener INSTANCE;

    public static void setup(@NotNull JavaPlugin plugin) {
        if (INSTANCE != null) return;

        INSTANCE = new MenuListener(plugin);
    }

    public static void setup() {
        if (INSTANCE != null) return;

        INSTANCE = new MenuListener(ApolloPlugin.getInstance());
    }

    public MenuListener(JavaPlugin plugin) {
        Common.registerListener(plugin, this);
    }


    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (Menu.hasMenu((Player)event.getWhoClicked())) {
                Menu menu = Menu.getMenu((Player)event.getWhoClicked());
                event.setCancelled(menu.autoCancel);
                MenuEvent menuEvent = new MenuEvent(event);
                MenuItem menuItem = menu.getMenuItem(event.getSlot());
                if (menuItem != null) {
                    menuItem.execute(menuEvent);
                    return;
                }

                if (event.isShiftClick() && event.isLeftClick()) {
                    menu.onShiftLeftClick(event);
                } else if (event.isShiftClick() && event.isRightClick()) {
                    menu.onShiftRightClick(event);
                } else if (event.isRightClick()) {
                    menu.onRightClick(event);
                } else if (event.isLeftClick()) {
                    menu.onLeftClick(event);
                }
            }

        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (Menu.hasMenu((Player)event.getWhoClicked())) {
            Menu menu = Menu.getMenu((Player)event.getWhoClicked());
            event.setCancelled(menu.autoCancel);
            menu.onDrag(event);
        }

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (Menu.hasMenu((Player)event.getPlayer())) {
            Menu menu = Menu.getMenu((Player)event.getPlayer());
            menu.onOpen(event);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (Menu.hasMenu((Player)event.getPlayer())) {
            Menu menu = Menu.getMenu((Player)event.getPlayer());
            menu.close((Player)event.getPlayer());
            menu.onClose(event);
        }

    }
}