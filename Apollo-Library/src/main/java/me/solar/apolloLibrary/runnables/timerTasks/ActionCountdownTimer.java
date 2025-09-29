package me.solar.apolloLibrary.runnables.timerTasks;

import me.solar.apolloLibrary.Common;
import org.bukkit.entity.Player;

public class ActionCountdownTimer extends CountdownTask {

    private Player[] players;

    private String actionBarMessage;

    public ActionCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    public ActionCountdownTimer(long time, String actionBarMessage, Player... players) {
        super(time);
        this.players = players;
        this.actionBarMessage = actionBarMessage;
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

            if (actionBarMessage != null) {
                Common.actionBar(player, actionBarMessage.replace("{time}", String.valueOf(getCountdownTime())));
                continue;
            }

            Common.actionBar(player, String.valueOf(getCountdownTime()));
        }
    }
}
