package me.solar.apolloLibrary.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for ItemStack operations.
 */
public class ItemStackUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ItemStackUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if two ItemStacks are similar.
     *
     * @param item1 the first ItemStack
     * @param item2 the second ItemStack
     * @return true if the ItemStacks are similar, false otherwise
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        return item1.isSimilar(item2);
    }

    public static void setDisplayName(ItemStack item, String displayName) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.customName(Common.component(displayName));
        item.setItemMeta(itemMeta);
    }

    public static void setLore(ItemStack item, String[] lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(Common.component(line));
        }

        itemMeta.lore(loreList);
        item.setItemMeta(itemMeta);
    }

    public static ItemStack createItemStack(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        setDisplayName(item, name);
        setLore(item, lore);
        return item;
    }

    public static ItemStack createItemStack(Material material) {
        return new ItemStack(material);
    }

    /**
     * Sets the custom model data for an ItemStack.
     *
     * @param item  the ItemStack to modify
     * @param model the custom model data as a namespaced key (e.g., "namespace:model")
     */
    public static void setItemModel(ItemStack item, String model) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setItemModel(Common.namespacedKey(model));
        item.setItemMeta(itemMeta);
    }

    public static void setItemModel(ItemStack item, NamespacedKey model) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setItemModel(model);
        item.setItemMeta(itemMeta);
    }

}
