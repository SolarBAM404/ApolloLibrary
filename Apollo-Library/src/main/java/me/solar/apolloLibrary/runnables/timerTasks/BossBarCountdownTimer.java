package me.solar.apolloLibrary.runnables.timerTasks;

import me.solar.apolloLibrary.Common;
import me.solar.apolloLibrary.runnables.TimerTask;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

/**
 * Countdown timer that displays time in a boss bar for players.
 */
public class BossBarCountdownTimer extends CountdownTask {

    /** The players to display the boss bar to. */
    private Player[] players;
    /** The color of the boss bar. */
    BossBar.Color color = BossBar.Color.RED;
    /** The overlay style of the boss bar. */
    BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
    /** The title of the boss bar. */
    String title = "Countdown";
    /** The boss bar instance. */
    BossBar bossBar;

    /**
     * Constructs a BossBarCountdownTimer with time and players.
     *
     * @param time the countdown time
     * @param players the players to display to
     */
    public BossBarCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    /**
     * Constructs a BossBarCountdownTimer with custom title, color, overlay, and players.
     *
     * @param time the countdown time
     * @param title the boss bar title
     * @param color the boss bar color
     * @param overlay the boss bar overlay
     * @param players the players to display to
     */
    public BossBarCountdownTimer(long time, String title, BossBar.Color color, BossBar.Overlay overlay, Player... players) {
        super(time);
        this.players = players;
        this.title = title;
        this.color = color;
        this.overlay = overlay;
    }

    /**
     * Called on each countdown tick to update the boss bar.
     */
    @Override
    public void action() {
        if (players == null || players.length == 0) {
            return;
        }

        float progress = (float) getCountdownTime() / (float) getTime();

        bossBar = BossBar.bossBar(Common.component(title), progress, color, overlay);
        bossBar.progress((float) ((double) getCountdownTime() / (double) getTime()));

        for (Player player : players) {
            if (player == null || !player.isOnline()) {
                continue;
            }
            bossBar.addViewer(player);
        }

    }
}
