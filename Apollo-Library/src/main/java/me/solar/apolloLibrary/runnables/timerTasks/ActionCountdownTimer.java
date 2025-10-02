package me.solar.apolloLibrary.runnables.timerTasks;

import me.solar.apolloLibrary.Common;
import org.bukkit.entity.Player;

/**
 * Countdown timer that displays time in the action bar for players.
 */
public class ActionCountdownTimer extends CountdownTask {

    /** The players to display the countdown to. */
    private final Player[] players;

    /** The action bar message to display. */
    private String actionBarMessage;

    /**
     * Constructs an ActionCountdownTimer with time and players.
     *
     * @param time the countdown time
     * @param players the players to display to
     */
    public ActionCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    /**
     * Constructs an ActionCountdownTimer with time, message, and players.
     *
     * @param time the countdown time
     * @param actionBarMessage the message to display
     * @param players the players to display to
     */
    public ActionCountdownTimer(long time, String actionBarMessage, Player... players) {
        super(time);
        this.players = players;
        this.actionBarMessage = actionBarMessage;
    }

    /**
     * Called on each countdown tick to update the action bar.
     */
    @Override
    public void action() {
        if (players == null) {
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
