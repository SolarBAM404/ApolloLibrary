package me.solar.apolloLibrary.runnables.timerTasks;

import me.solar.apolloLibrary.Common;
import me.solar.apolloLibrary.runnables.TimerTask;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossBarCountdownTimer extends CountdownTask {

    private Player[] players;
    BossBar.Color color = BossBar.Color.RED;
    BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
    String title = "Countdown";
    BossBar bossBar;

    public BossBarCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    public BossBarCountdownTimer(long time, String title, BossBar.Color color, BossBar.Overlay overlay, Player... players) {
        super(time);
        this.players = players;
        this.title = title;
        this.color = color;
        this.overlay = overlay;
    }

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
