package me.solar.apolloLibrary.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public abstract class RunnableObject implements Consumer<BukkitTask> {

    @Override
    public abstract void accept(BukkitTask bukkitTask);

}
