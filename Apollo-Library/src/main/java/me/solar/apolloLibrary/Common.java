package me.solar.apolloLibrary;

import lombok.Getter;
import me.solar.apolloLibrary.core.ApolloPlugin;
import me.solar.apolloLibrary.exceptions.PluginException;
import me.solar.apolloLibrary.runnables.RunnableObject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Common {

    private Common() {
        throw new IllegalStateException("Utility class" );
    }

    // ------------------- Prefixes ------------------ //

    @Getter
    private static String tellPrefix = "";

    @Getter
    private static Component tellPrefixComponent = component("");

    @Getter
    private static String logPrefix = "<gray>[<gold>" + ApolloPlugin.getPluginName() + "<gray>] ";

    @Getter
    private static Component logPrefixComponent = component("");

    public static void setTellPrefix(String prefix) {
        tellPrefix = prefix;
        tellPrefixComponent = component(prefix);
    }

    public static void setLogPrefix(String prefix) {
        logPrefix = prefix;
        logPrefixComponent = component(prefix);
    }

    // ------------------- Messages ------------------ //

    public static Component component(String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }

    public static void tell(Audience audience, String message) {
        Component component = component(message);
        audience.sendMessage(component);
    }

    public static void tellNoPrefix(Audience audience, String message) {
        Component component = component(message);
        audience.sendMessage(component);
    }

    public static void tellLater(Audience audience, String message, long delayTicks) {
        throw new NotImplementedException();
    }

    public static void tellLaterNoPrefix(Audience audience, String message, long delayTicks) {
        throw new NotImplementedException();
    }

    public static void tellConversation(Conversation conversation, String message) {
        Component component = component(message);
    }

    public static void broadcast(String message) {
        Component component = component(message);
        ApolloLibrary.getBukkitServer().broadcast(component);
    }

    public static void broadcastTo(String message, Iterable<CommandSender> recipients) {
        Component component = component(message);
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(component);
        }
    }

    public static void log(String message) {
        Component component = component(message);
        ApolloLibrary.getBukkitServer().getConsoleSender().sendMessage(component);
    }

    public static void actionBar(Audience audience, String message) {
        Component component = component(message);
        audience.sendActionBar(component);
    }

    // ------------------- Aesthetics ------------------ //

    public static String consoleLine() {
        return "!----------------------------------------";
    }

    public static String consoleLineSmooth() {
        return "______________________________________________________________";
    }

    public static String chatLine() {
        return "*----------------------------------------";
    }

    public static String chatLineSmooth() {
        return "<underline>______________________________________________________________</underline>";
    }

    // ------------------- String Utils ------------------ //

    public static String limitString(String string, int maxLength) {
        if (string.length() <= maxLength) {
            return string;
        }
        return string.substring(0, maxLength - 3) + "...";
    }


    // ------------------- Plugin Utils ------------------ //

    /**
     * Checks if a plugin is enabled. We also schedule an async task to make
     * sure the plugin is loaded correctly when the server is done booting
     * <p>
     * Return true if it is loaded (this does not mean it works correctly)
     *
     * @param pluginName
     * @return
     */
    public static boolean doesPluginExist(final String pluginName) {
        Plugin lookup = null;

        for (final Plugin otherPlugin : Bukkit.getPluginManager().getPlugins())
            if (otherPlugin.getDescription().getName().equals(pluginName)) {
                lookup = otherPlugin;

                break;
            }

        final Plugin found = lookup;

        if (found == null)
            return false;

        if (!found.isEnabled())
            throw new PluginException("Plugin " + pluginName + " is not enabled!");

        return true;
    }

    // ------------------- Runnables ------------------ //

    public static void runTask(JavaPlugin plugin, RunnableObject runnableObject) {
        Bukkit.getServer().getScheduler().runTask(plugin, runnableObject);
    }

    public static void runTaskLater(JavaPlugin plugin, RunnableObject runnableObject, long delayTicks) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, runnableObject, delayTicks);
    }

    public static void runTaskTimer(JavaPlugin plugin, RunnableObject runnableObject, long delayTicks, long periodTicks) {
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnableObject, delayTicks, periodTicks);
    }

    public static RunnableObject runTask(JavaPlugin plugin, Runnable runnable) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTask(plugin, runnableObject);
        return runnableObject;
    }

    public static RunnableObject runTaskLater(JavaPlugin plugin, Runnable runnable, long delayTicks) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTaskLater(plugin, runnableObject, delayTicks);
        return runnableObject;
    }

    public static RunnableObject runTaskTimer(JavaPlugin plugin, Runnable runnable, long delayTicks, long periodTicks) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnableObject, delayTicks, periodTicks);
        return runnableObject;
    }

}
