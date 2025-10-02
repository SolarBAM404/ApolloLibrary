package me.solar.apolloLibrary.runnables.timerTasks;

import org.bukkit.entity.Player;

/**
 * Countdown timer that displays progress in the player's XP bar.
 */
public class XpProgressCountdownTimer extends CountdownTask {

    /**
     * The players to update XP bar progress for.
     */
    private Player[] players;

    /**
     * Constructs an XpProgressCountdownTimer with the specified time and players.
     * @param time the countdown time
     * @param players the players to update
     */
    public XpProgressCountdownTimer(long time, Player... players) {
        super(time);
        this.players = players;
    }

    /**
     * Called on each countdown tick to update the XP bar progress for each player.
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
            float progress = (float) getCountdownTime() / (float) getTime();
            player.setExp(progress);
        }
    }
}
