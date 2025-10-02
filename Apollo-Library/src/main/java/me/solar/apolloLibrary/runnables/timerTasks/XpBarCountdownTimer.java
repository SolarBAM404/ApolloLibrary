package me.solar.apolloLibrary.runnables.timerTasks;

import org.bukkit.entity.Player;

/**
 * Countdown timer that displays the remaining time in the player's XP bar level.
 */
public class XpBarCountdownTimer extends CountdownTask {

    /**
     * The players to update XP bar level for.
     */
    private Player[] players;

    /**
     * Constructs an XpBarCountdownTimer with the specified time and players.
     * @param time the countdown time
     * @param players the players to update
     */
    public XpBarCountdownTimer(long time, Player... players) {
        super(time);
    }

    /**
     * Called on each countdown tick to update the XP bar level for each player.
     */
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
