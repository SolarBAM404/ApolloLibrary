package me.solar.apolloLibrary;

import net.kyori.adventure.text.Component;
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

}
