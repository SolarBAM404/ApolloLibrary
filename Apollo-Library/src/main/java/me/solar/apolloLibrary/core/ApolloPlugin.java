package me.solar.apolloLibrary.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ApolloPlugin extends JavaPlugin {


    @Getter
    private static String name;

    @Getter
    private static String version;

    @Override
    public void onEnable() {
        // Plugin startup logic

        onLoad();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        onShutdown();
    }

    public abstract void onLoad();
    public abstract void onReload();
    public abstract void onShutdown();

}
