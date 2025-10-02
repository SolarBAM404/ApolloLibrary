package me.solar.apolloLibrary;

import org.bukkit.inventory.ItemStack;

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

}
