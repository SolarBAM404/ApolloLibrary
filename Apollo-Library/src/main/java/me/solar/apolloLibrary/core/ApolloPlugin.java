package me.solar.apolloLibrary.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    public static <T extends ApolloPlugin> T getStaticInstance() {
        try {
            Class<?> clazz = ApolloPlugin.class;
            Field instanceField = clazz.getDeclaredField("instance");
            if (Modifier.isStatic(instanceField.getModifiers())) {
                instanceField.setAccessible(true);
                return (T) instanceField.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle or log exception as needed
            e.printStackTrace();
        }
        throw new IllegalStateException("ApolloPlugin instance is not static");
    }


}
