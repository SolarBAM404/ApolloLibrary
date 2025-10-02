package me.solar.apolloLibrary.runnables.timerTasks;

import lombok.Getter;
import me.solar.apolloLibrary.runnables.TimerTask;

/**
 * A timer task that counts down from a specified time and calls {@link #action()} each tick.
 */
public class CountdownTask extends TimerTask {

    /**
     * The remaining countdown time.
     */
    @Getter
    private long countdownTime;

    /**
     * Constructs a CountdownTask with the specified time.
     * @param time the countdown time
     */
    public CountdownTask(long time) {
        super(time);
        this.countdownTime = time;
    }

    /**
     * Called on each timer tick. Decrements countdown and calls {@link #action()}.
     */
    @Override
    public void run() {

        if (countdownTime <= 0) {
            return;
        }
        action();
        countdownTime--;

    }

    /**
     * The action to perform on each countdown tick.
     * Override in subclasses for custom behavior.
     */
    public void action() {
    }
}
