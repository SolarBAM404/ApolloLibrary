package me.solar.apolloLibrary.menus;

import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public record MenuEvent(Player player, Event event, Menu menu, ItemStack item, MenuClickType clickType) {
    public MenuEvent(InventoryClickEvent event) {
        this((Player) event.getWhoClicked(), event, Menu.getMenu((Player) event.getWhoClicked()), event.getCurrentItem(), MenuClickType.fromClickType(event.getClick()));
    }

    public MenuEvent(Player player, Event event, Menu menu, ItemStack item) {
        this(player, event, menu, item, null);
    }

    @Override
    @Generated
    public Player player() {
        return this.player;
    }

    @Override
    @Generated
    public Event event() {
        return this.event;
    }

    @Override
    @Generated
    public Menu menu() {
        return this.menu;
    }

    @Override
    @Generated
    public ItemStack item() {
        return this.item;
    }

    @Override
    @Generated
    public MenuClickType clickType() {
        return this.clickType;
    }
}
