package me.solar.apolloLibrary.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

    public PlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isPlayer(Object object) {
        return object instanceof Player;
    }

    /// Packet Utilities

    public static void sendBlock(Player player, Location location, Material material) {
        player.sendBlockChange(location, material.createBlockData());
    }

    public static void sendBlock(Player player, Location location, BlockData blockData) {
        player.sendBlockChange(location, blockData);
    }


    /// Inventory Utilities

    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    public static ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static ItemStack getItemInOffHand(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    public static boolean itemInHand(Player player, ItemStack itemStack) {
        return player.getInventory().getItemInMainHand().equals(itemStack);
    }

    public static boolean itemInOffHand(Player player, ItemStack itemStack) {
        return player.getInventory().getItemInOffHand().equals(itemStack);
    }

    public static boolean itemInEitherHand(Player player, ItemStack itemStack) {
        return player.getInventory().getItemInMainHand().equals(itemStack) || player.getInventory().getItemInOffHand().equals(itemStack);
    }


    /// Sound Utilities

    public static void playSound(Player player, String sound) {
        playSound(player, sound, 1.0F, 1.0F);
    }

    public static void playSound(Player player, String sound, float volume) {
        playSound(player, sound, volume, 1.0F);
    }

    public static void playSound(Player player, String sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void playSound(Player player, Sound sound) {
        playSound(player, sound, 1.0F, 1.0F);
    }

    public static void playSound(Player player, Sound sound, float volume) {
        playSound(player, sound, volume, 1.0F);
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
