package me.solar.apolloLibrary.runnables;

import me.solar.apolloLibrary.core.ApolloPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Abstract base class for runnable objects used in scheduling tasks.
 */
public abstract class RunnableObject extends BukkitRunnable {

    /**
     * Creates a RunnableObject from a Runnable.
     *
     * @param runnable the runnable to wrap
     * @return a RunnableObject instance
     */
    public static RunnableObject of(Runnable runnable) {
        return new RunnableObject() {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    // --- Final overrides for BukkitRunnable scheduling methods ---

    @Override
    public final synchronized @NotNull BukkitTask runTask(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        return super.runTask(plugin);
    }

    @Override
    public final synchronized @NotNull BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskAsynchronously(plugin);
    }

    @Override
    public final synchronized @NotNull BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskLater(plugin, delay);
    }

    @Override
    public final synchronized @NotNull BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    public final synchronized @NotNull BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskTimer(plugin, delay, period);
    }

    @Override
    public final synchronized @NotNull BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }


    // --- Overloads using ApolloPlugin.getInstance() ---

    /**
     * Runs the task using ApolloPlugin.getInstance().
     *
     * @return the BukkitTask
     */
    public BukkitTask runTask() {
        return super.runTask(ApolloPlugin.getInstance());
    }

    /**
     * Runs the task asynchronously using ApolloPlugin.getInstance().
     *
     * @return the BukkitTask
     */
    public BukkitTask runTaskAsynchronously() {
        return super.runTaskAsynchronously(ApolloPlugin.getInstance());
    }

    /**
     * Runs the task later using ApolloPlugin.getInstance().
     *
     * @param delay the delay in ticks
     * @return the BukkitTask
     */
    public BukkitTask runTaskLater(long delay) {
        return super.runTaskLater(ApolloPlugin.getInstance(), delay);
    }

    /**
     * Runs the task later asynchronously using ApolloPlugin.getInstance().
     *
     * @param delay the delay in ticks
     * @return the BukkitTask
     */
    public BukkitTask runTaskLaterAsynchronously(long delay) {
        return super.runTaskLaterAsynchronously(ApolloPlugin.getInstance(), delay);
    }

    /**
     * Runs the task repeatedly using ApolloPlugin.getInstance().
     *
     * @param delay  the initial delay in ticks
     * @param period the period in ticks
     * @return the BukkitTask
     */
    public BukkitTask runTaskTimer(long delay, long period) {
        return super.runTaskTimer(ApolloPlugin.getInstance(), delay, period);
    }

    /**
     * Runs the task repeatedly asynchronously using ApolloPlugin.getInstance().
     *
     * @param delay  the initial delay in ticks
     * @param period the period in ticks
     * @return the BukkitTask
     */
    public BukkitTask runTaskTimerAsynchronously(long delay, long period) {
        return super.runTaskTimerAsynchronously(ApolloPlugin.getInstance(), delay, period);
    }
}
