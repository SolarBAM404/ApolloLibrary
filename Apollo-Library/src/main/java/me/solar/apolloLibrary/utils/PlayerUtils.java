package me.solar.apolloLibrary.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public PlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isPlayer(Object object) {
        return object instanceof Player;
    }

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
