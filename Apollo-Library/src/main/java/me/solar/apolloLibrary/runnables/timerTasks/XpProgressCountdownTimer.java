package me.solar.apolloLibrary.runnables.timerTasks;

import org.bukkit.entity.Player;

public class XpProgressCountdownTimer extends CountdownTask {

    private Player[] players;

    public XpProgressCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    @Override
    public void action() {
        if (players == null || players.length == 0) {
            return;
        }
        for (Player player : players) {
            if (player == null || !player.isOnline()) {
                continue;
            }
            float progress = (float) getCountdownTime() / (float) getTime();
            player.setExp(progress);
        }
    }
}
