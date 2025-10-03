package me.solar.apolloLibrary.runnables;

import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/**
 * Abstract base class for runnable objects used in scheduling tasks.
 */
public abstract class RunnableObject implements Consumer<BukkitTask> {

    private BukkitTask bukkitTask;

    /**
     * Called when the scheduled task is executed.
     *
     * @param bukkitTask the BukkitTask instance
     */
    @Override
    public void accept(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public void cancel() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    /**
     * Creates a RunnableObject from a Runnable.
     *
     * @param runnable the runnable to wrap
     * @return a RunnableObject instance
     */
    public static RunnableObject of(Runnable runnable) {
        return new RunnableObject() {
            @Override
            public void accept(BukkitTask bukkitTask) {
                runnable.run();
            }
        };
    }

}
