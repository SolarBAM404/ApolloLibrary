package me.solar.apollo.apolloCore.utils;

import me.solar.apollo.apolloCore.Common;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class ItemStackUtils {

    private ItemStackUtils() {}

    public static ItemStack setName(ItemStack itemStack, String name) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.customName(Common.colorize(name));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;

        List<Component> components = new ArrayList<>();
        for (String line : lore) {
            components.add(Common.colorize(line));
        }
        meta.lore(components);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setNameAndLore(ItemStack itemStack, String name, List<String> lore) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        setName(itemStack, name);
        setLore(itemStack, lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setCustomData(ItemStack itemStack, String key, String value) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().set(NamespacedKey.fromString(key), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setCustomData(ItemStack itemStack, String key, int value) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().set(NamespacedKey.fromString(key), PersistentDataType.INTEGER, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setItemModel(ItemStack itemStack, String modelData) {
        var meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setCustomModelData(Integer.parseInt(modelData));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}

