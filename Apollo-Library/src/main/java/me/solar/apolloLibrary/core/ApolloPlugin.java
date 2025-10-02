package me.solar.apolloLibrary.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for Apollo plugins.
 */
public abstract class ApolloPlugin extends JavaPlugin {

    /**
     * The name of the plugin.
     */
    @Getter
    private static String pluginName;

    /**
     * The version of the plugin.
     */
    @Getter
    private static String version;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        onLoad();
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        onShutdown();
    }

    /**
     * Called when the plugin is loaded.
     */
    public abstract void onLoad();

    /**
     * Called when the plugin is reloaded.
     */
    public abstract void onReload();

    /**
     * Called when the plugin is shut down.
     */
    public abstract void onShutdown();

}
