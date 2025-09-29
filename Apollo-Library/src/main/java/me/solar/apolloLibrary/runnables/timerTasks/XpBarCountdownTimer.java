package me.solar.apolloLibrary.runnables.timerTasks;

import org.bukkit.entity.Player;

public class XpBarCountdownTimer extends CountdownTask {

    private Player[] players;

    public XpBarCountdownTimer(long time, Player... players) {
        super(time);
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
            player.setLevel((int) getCountdownTime());
        }

    }
}
