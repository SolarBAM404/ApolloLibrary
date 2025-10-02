package me.solar.apolloLibrary;

import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

    private ItemStackUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        return item1.isSimilar(item2);
    }

}
