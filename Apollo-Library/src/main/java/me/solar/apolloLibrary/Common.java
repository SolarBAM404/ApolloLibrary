package me.solar.apolloLibrary;

import lombok.Getter;
import me.solar.apolloLibrary.core.ApolloPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
    private static String logPrefix = "<gray>[<gold>" + ApolloPlugin.getName() + "<gray>] ";

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


    public static Component component(String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }

    public static void tell(Player player, String message) {
        Component component = component(message);
        player.sendMessage(component);
    }

    public static void tellNoPrefix(Player player, String message) {
        Component component = component(message);
        player.sendMessage(component);
    }

    public static void tellLater(Player player, String message, long delayTicks) {
        throw new NotImplementedException();
    }

    public static void tellLaterNoPrefix(Player player, String message, long delayTicks) {
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

    public static String limitString(String string, int maxLength) {
        if (string.length() <= maxLength) {
            return string;
        }
        return string.substring(0, maxLength - 3) + "...";
    }

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
            return false; // TODO handle disabled plugins
//            runLaterAsync(0, () -> Valid.checkBoolean(found.isEnabled(), SimplePlugin.getNamed() + " could not hook into " + pluginName + " as the plugin is disabled! (DO NOT REPORT THIS TO " + SimplePlugin.getNamed() + ", look for errors above and contact support of '" + pluginName + "')"));

        return true;
    }

}
