package me.solar.apolloLibrary.menus;

import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuEvent {
    private final Player player;
    private final Event event;
    private final Menu menu;
    private final ItemStack item;
    private final MenuClickType clickType;

    public MenuEvent(InventoryClickEvent event) {
        this((Player)event.getWhoClicked(), event, Menu.getMenu((Player)event.getWhoClicked()), event.getCurrentItem(), MenuClickType.fromClickType(event.getClick()));
    }

    public MenuEvent(Player player, Event event, Menu menu, ItemStack item) {
        this(player, event, menu, item, (MenuClickType)null);
    }

    public MenuEvent(Player player, Event event, Menu menu, ItemStack item, MenuClickType clickType) {
        this.player = player;
        this.event = event;
        this.menu = menu;
        this.item = item;
        this.clickType = clickType;
    }

    @Generated
    public Player getPlayer() {
        return this.player;
    }

    @Generated
    public Event getEvent() {
        return this.event;
    }

    @Generated
    public Menu getMenu() {
        return this.menu;
    }

    @Generated
    public ItemStack getItem() {
        return this.item;
    }

    @Generated
    public MenuClickType getClickType() {
        return this.clickType;
    }
}
