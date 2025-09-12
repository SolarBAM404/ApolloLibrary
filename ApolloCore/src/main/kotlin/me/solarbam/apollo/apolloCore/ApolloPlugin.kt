package me.solarbam.apollo.apolloCore

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin

abstract class ApolloPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: ApolloPlugin

        @JvmStatic
        var adventure : BukkitAudiences? = null
            get() {
                if(this.adventure == null) {
                    throw IllegalStateException("Tried to access Adventure when the plugin was disabled!");
                }
                return this.adventure;
            }
    }

    final override fun onEnable() {
        // Plugin startup logic
        instance = this
        adventure = BukkitAudiences.create(this)
        start()
    }

    final override fun onDisable() {
        // Plugin shutdown logic
        shutdown()

        if(adventure != null) {
            adventure!!.close();
            adventure = null;
        }
    }

    abstract fun start()
    abstract fun shutdown()
}