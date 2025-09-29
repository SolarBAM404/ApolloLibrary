package me.solar.apolloLibrary.runnables;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

public abstract class TimerTask extends RunnableObject {

    @Getter
    private final long time;

    private long currentTime = 0;

    public TimerTask(long time) {
        this.time = time;
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        currentTime++;
        if (currentTime >= time) {
            bukkitTask.cancel();
            return;
        }
        run();
    }

    public abstract void run();
}
