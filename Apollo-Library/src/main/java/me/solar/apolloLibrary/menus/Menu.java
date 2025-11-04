package me.solar.apolloLibrary.menus;

import lombok.Generated;
import me.solar.apolloLibrary.utils.Common;
import me.solar.apolloLibrary.utils.ItemStackUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu {

    private static final Map<Player, Menu> menus = new HashMap<>();
    private final Component name;
    protected final Inventory inventory;
    private final Map<Integer, MenuItem> menuItems = new HashMap<>();
    protected boolean autoCancel;

    public static void addMenu(Menu menu, Player player) {
        menus.put(player, menu);
    }

    public static void removeMenu(Player player) {
        menus.remove(player);
    }

    public static Menu getMenu(Player player) {
        return menus.get(player);
    }

    public static boolean hasMenu(Player player) {
        return menus.containsKey(player);
    }

    protected Menu(String name, int size) {
        this(Common.component(name), size);
    }

    protected Menu(Component name, int size) {
        this.autoCancel = true;
        this.name = name;
        this.inventory = Bukkit.createInventory(null, size, name);
        MenuListener.setup();
    }

    protected abstract void initialize();

    public void open(Player player) {
        if (hasMenu(player)) {
            getMenu(player).close(player);
        }

        this.initialize();
        addMenu(this, player);
        player.openInventory(this.inventory);
    }

    public void close(Player player) {
        removeMenu(player);
        this.inventory.close();
    }

    public void update(Player player) {
        this.initialize();
        player.updateInventory();
    }

    public ItemStack setItem(int slot, Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemStackUtils.setDisplayName(item, name);
        this.inventory.setItem(slot, item);
        return item;
    }

    public ItemStack setItem(int slot, Material material, String name, String[] lore) {
        ItemStack item = new ItemStack(material);
        ItemStackUtils.setDisplayName(item, name);
        ItemStackUtils.setLore(item, lore);
        this.inventory.setItem(slot, item);
        return item;
    }

    public ItemStack setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
        return item;
    }

    public ItemStack setItem(int slot, MenuItem item) {
        this.inventory.setItem(slot, item);
        this.menuItems.put(slot, item);
        return item;
    }

    public MenuItem getMenuItem(int slot) {
        return this.menuItems.get(slot);
    }

    public void onDrag(InventoryDragEvent event) {
    }

    public void onRightClick(InventoryClickEvent event) {
    }

    public void onLeftClick(InventoryClickEvent event) {
    }

    public void onShiftLeftClick(InventoryClickEvent event) {
    }

    public void onShiftRightClick(InventoryClickEvent event) {
    }

    public void onOpen(InventoryOpenEvent event) {
    }

    public void onClose(InventoryCloseEvent event) {
    }

    @Generated
    public static Map<Player, Menu> getMenus() {
        return menus;
    }

    @Generated
    public Component getName() {
        return this.name;
    }

    @Generated
    public Inventory getInventory() {
        return this.inventory;
    }

    @Generated
    public Map<Integer, MenuItem> getMenuItems() {
        return this.menuItems;
    }

}
