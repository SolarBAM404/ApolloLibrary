package me.solar.apolloLibrary.runnables;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

/**
 * Abstract class for timer-based tasks.
 */
public abstract class TimerTask extends RunnableObject {

    /** The total time for the timer. */
    @Getter
    private final long time;

    /** The current time elapsed. */
    private long currentTime = 0;

    /**
     * Constructs a TimerTask with the specified time.
     *
     * @param time the total time for the timer
     */
    public TimerTask(long time) {
        this.time = time;
    }

    /**
     * Called on each tick of the timer.
     *
     * @param bukkitTask the BukkitTask instance
     */
    @Override
    public void accept(BukkitTask bukkitTask) {
        if (currentTime >= time) {
            bukkitTask.cancel();
            return;
        }
        currentTime++;
        run();
    }

    /**
     * The action to perform on each timer tick.
     */
    public abstract void run();
}
