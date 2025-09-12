package me.solarbam.apollo.apolloCore.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static long getStatistic(Statistic statistic, OfflinePlayer player) {
        return player.getStatistic(statistic);
    }

    public static long getPing(Player player) {
        return player.getPing();
    }

}
