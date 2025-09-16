package me.solar.apolloLibrary;

import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public final class ApolloLibrary extends JavaPlugin {

    @Getter
    private static ApolloLibrary instance;

    @Getter
    private static Server bukkitServer;


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[ApolloLibrary] Enabled");
        instance = this;
        bukkitServer = this.getServer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[ApolloLibrary] Disabled");
    }
}
