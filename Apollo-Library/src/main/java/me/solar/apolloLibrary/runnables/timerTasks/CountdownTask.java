package me.solar.apolloLibrary.runnables.timerTasks;

import lombok.Getter;
import me.solar.apolloLibrary.runnables.TimerTask;

public class CountdownTask extends TimerTask {

    @Getter
    private long countdownTime;

    public CountdownTask(long time) {
        super(time);
        this.countdownTime = time;
    }

    @Override
    public void run() {

        if (countdownTime <= 0) {
            return;
        }
        action();
        countdownTime--;

    }

    public void action() {
    }
}
