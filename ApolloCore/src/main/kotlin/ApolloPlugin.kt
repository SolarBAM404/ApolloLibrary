import org.bukkit.plugin.java.JavaPlugin

abstract class ApolloPlugin : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        start()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        shutdown()
    }

    abstract fun start()
    abstract fun shutdown()
}