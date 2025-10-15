package me.solar.apolloLibrary.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.solar.apolloLibrary.utils.Common;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Abstract base class for Apollo plugins.
 */
public abstract class ApolloPlugin extends JavaPlugin {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private static ApolloPlugin instance;

    /**
     * The name of the plugin.
     */
    @Getter
    @Setter
    private static String pluginName;

    /**
     * The version of the plugin.
     */
    @Getter
    @Setter
    private static String version;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public final void onEnable() {
        // Plugin startup logic
        startup();
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public final void onDisable() {
        // Plugin shutdown logic
        shutdown();
    }

    public final void reload() {
        Common.log("Reloading plugin");
        onReload();
        Common.log("Plugin reloaded");
    }

    /**
     * Called when the plugin is loaded.
     */
    public abstract void startup();

    /**
     * Called when the plugin is reloaded.
     */
    public abstract void onReload();

    /**
     * Called when the plugin is shut down.
     */
    public abstract void shutdown();

}
