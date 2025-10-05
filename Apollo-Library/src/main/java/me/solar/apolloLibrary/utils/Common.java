package me.solar.apolloLibrary.utils;

import com.google.common.collect.Range;
import lombok.Getter;
import me.solar.apolloLibrary.ApolloLibrary;
import me.solar.apolloLibrary.core.ApolloPlugin;
import me.solar.apolloLibrary.exceptions.PluginException;
import me.solar.apolloLibrary.runnables.RunnableObject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

/**
 * Common utility methods for messaging, plugin checks, and scheduling tasks.
 */
public class Common {

    /**
     * Private constructor to prevent instantiation.
     */
    private Common() {
        throw new IllegalStateException("Utility class" );
    }

    private static final Random RANDOM = new Random();

    // ------------------- Prefixes ------------------ //

    @Getter
    private static String tellPrefix = "";

    @Getter
    private static Component tellPrefixComponent = component("");

    @Getter
    private static String logPrefix = "<gray>[<gold>" + ApolloPlugin.getPluginName() + "<gray>] ";

    @Getter
    private static Component logPrefixComponent = component("");

    /**
     * Sets the tell prefix for messages.
     * @param prefix the prefix string
     */
    public static void setTellPrefix(String prefix) {
        tellPrefix = prefix;
        tellPrefixComponent = component(prefix);
    }

    /**
     * Sets the log prefix for messages.
     * @param prefix the prefix string
     */
    public static void setLogPrefix(String prefix) {
        logPrefix = prefix;
        logPrefixComponent = component(prefix);
    }

    // ------------------- Messages ------------------ //

    /**
     * Converts a string message to a Component using MiniMessage.
     * @param message the message string
     * @return the Component representation
     */
    public static Component component(String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }

    /**
     * Sends a message to the given audience with prefix.
     * @param audience the audience to send to
     * @param message the message string
     */
    public static void tell(Audience audience, String message) {
        Component component = component(message);
        audience.sendMessage(component);
    }

    public static void tell(Audience audience, String... messages) {
        for (String message : messages) {
            tell(audience, message);
        }
    }

    /**
     * Sends a message to the given audience without prefix.
     * @param audience the audience to send to
     * @param message the message string
     */
    public static void tellNoPrefix(Audience audience, String message) {
        Component component = component(message);
        audience.sendMessage(component);
    }

    /**
     * Schedules a message to be sent later to the audience.
     * @param audience the audience
     * @param message the message
     * @param delayTicks delay in ticks
     */
    public static void tellLater(Audience audience, String message, long delayTicks) {
        throw new NotImplementedException();
    }

    /**
     * Schedules a message to be sent later to the audience without prefix.
     * @param audience the audience
     * @param message the message
     * @param delayTicks delay in ticks
     */
    public static void tellLaterNoPrefix(Audience audience, String message, long delayTicks) {
        throw new NotImplementedException();
    }

    /**
     * Sends a message in a conversation.
     * @param conversation the conversation
     * @param message the message
     */
    public static void tellConversation(Conversation conversation, String message) {
        Component component = component(message);
    }

    /**
     * Broadcasts a message to all players.
     * @param message the message to broadcast
     */
    public static void broadcast(String message) {
        Component component = component(message);
        ApolloLibrary.getBukkitServer().broadcast(component);
    }

    /**
     * Broadcasts a message to specific recipients.
     * @param message the message
     * @param recipients the recipients
     */
    public static void broadcastTo(String message, Iterable<CommandSender> recipients) {
        Component component = component(message);
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(component);
        }
    }

    /**
     * Logs a message to the console.
     * @param message the message
     */
    public static void log(String message) {
        Component component = component(message);
        ApolloLibrary.getBukkitServer().getConsoleSender().sendMessage(component);
    }

    /**
     * Sends an action bar message to the audience.
     * @param audience the audience
     * @param message the message
     */
    public static void actionBar(Audience audience, String message) {
        Component component = component(message);
        audience.sendActionBar(component);
    }

    // ------------------- Aesthetics ------------------ //

    /**
     * Returns a decorative console line.
     * @return the console line string
     */
    public static String consoleLine() {
        return "!----------------------------------------";
    }

    /**
     * Returns a smooth decorative console line.
     * @return the smooth console line string
     */
    public static String consoleLineSmooth() {
        return "______________________________________________________________";
    }

    /**
     * Returns a decorative chat line.
     * @return the chat line string
     */
    public static String chatLine() {
        return "*----------------------------------------";
    }

    /**
     * Returns a smooth decorative chat line.
     * @return the smooth chat line string
     */
    public static String chatLineSmooth() {
        return "<underline>______________________________________________________________</underline>";
    }

    // ------------------- String Utils ------------------ //

    /**
     * Limits the length of a string, appending "..." if truncated.
     * @param string the string to limit
     * @param maxLength the maximum length
     * @return the limited string
     */
    public static String limitString(String string, int maxLength) {
        if (string.length() <= maxLength) {
            return string;
        }
        return string.substring(0, maxLength - 3) + "...";
    }


    // ------------------- Plugin Utils ------------------ //

    /**
     * Checks if a plugin exists and is enabled.
     * @param pluginName the name of the plugin
     * @return true if the plugin exists and is enabled
     * @throws PluginException if the plugin is not enabled
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

    public static void registerListener(JavaPlugin plugin, Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    // ------------------- Runnables ------------------ //

    /**
     * Runs a task synchronously.
     * @param plugin the plugin instance
     * @param runnableObject the runnable object
     */
    public static void runTask(JavaPlugin plugin, RunnableObject runnableObject) {
        Bukkit.getServer().getScheduler().runTask(plugin, runnableObject);
    }

    /**
     * Runs a task later after a delay.
     * @param plugin the plugin instance
     * @param runnableObject the runnable object
     * @param delayTicks delay in ticks
     */
    public static void runTaskLater(JavaPlugin plugin, RunnableObject runnableObject, long delayTicks) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, runnableObject, delayTicks);
    }

    /**
     * Runs a repeating task with a delay and period.
     * @param plugin the plugin instance
     * @param runnableObject the runnable object
     * @param delayTicks initial delay in ticks
     * @param periodTicks period between executions in ticks
     */
    public static void runTaskTimer(JavaPlugin plugin, RunnableObject runnableObject, long delayTicks, long periodTicks) {
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnableObject, delayTicks, periodTicks);
    }

    /**
     * Runs a Runnable synchronously.
     * @param plugin the plugin instance
     * @param runnable the runnable
     * @return the RunnableObject wrapper
     */
    public static RunnableObject runTask(JavaPlugin plugin, Runnable runnable) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTask(plugin, runnableObject);
        return runnableObject;
    }

    /**
     * Runs a Runnable later after a delay.
     * @param plugin the plugin instance
     * @param runnable the runnable
     * @param delayTicks delay in ticks
     * @return the RunnableObject wrapper
     */
    public static RunnableObject runTaskLater(JavaPlugin plugin, Runnable runnable, long delayTicks) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTaskLater(plugin, runnableObject, delayTicks);
        return runnableObject;
    }

    /**
     * Runs a repeating Runnable with a delay and period.
     * @param plugin the plugin instance
     * @param runnable the runnable
     * @param delayTicks initial delay in ticks
     * @param periodTicks period between executions in ticks
     * @return the RunnableObject wrapper
     */
    public static RunnableObject runTaskTimer(JavaPlugin plugin, Runnable runnable, long delayTicks, long periodTicks) {
        RunnableObject runnableObject = RunnableObject.of(runnable);
        Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnableObject, delayTicks, periodTicks);
        return runnableObject;
    }

    public static void cancelTask(BukkitTask bukkitTask) {
        bukkitTask.cancel();
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void callEventLater(int delayTicks, Event event) {
        runTaskLater(ApolloPlugin.getStaticInstance(), () -> callEvent(event), (long)delayTicks);
    }

    public static int getRandomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static int getRandomInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static double getRandomDouble(double min, double max) {
        return RANDOM.nextDouble(min, max);
    }

    public static double getRandomDouble(double max) {
        return RANDOM.nextDouble(max);
    }

    public static float getRandomFloat(float min, float max) {
        return RANDOM.nextFloat(min, max);
    }

    public static float getRandomFloat(float max) {
        return RANDOM.nextFloat(max);
    }

    public static boolean chance(int percent) {
        return RANDOM.nextDouble() * (double)100.0F < (double)percent;
    }

    public static boolean chanceDouble(double percent) {
        return RANDOM.nextDouble() < percent;
    }

    public static <T> T getRandomElement(Collection<T> array) {
        if (array.isEmpty()) {
            return null;
        } else if (array.size() == 1) {
            return (T)array.toArray()[0];
        } else {
            int index = getRandomInt(0, array.size() - 1);
            return (T)array.toArray()[index];
        }
    }

    public static <T> T getRandomElement(T[] array) {
        if (array.length == 0) {
            return null;
        } else if (array.length == 1) {
            return (T)array[0];
        } else {
            int index = getRandomInt(0, array.length - 1);
            return (T)array[index];
        }
    }

    public static int getRandomInt(Range<Integer> range) {
        return RANDOM.nextInt((Integer)range.upperEndpoint() - (Integer)range.lowerEndpoint() + 1) + (Integer)range.lowerEndpoint();
    }

    public static double getRandomDouble(Range<Double> range) {
        return RANDOM.nextDouble() * ((Double)range.upperEndpoint() - (Double)range.lowerEndpoint()) + (Double)range.lowerEndpoint();
    }

    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static String convertToString(Location location) {
        int var10000 = location.getBlockX();
        return var10000 + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static String convertToString(Location location, boolean includeWorld) {
        String var10000 = includeWorld ? location.getWorld().getName() + "," : "";
        return var10000 + convertToString(location);
    }

    public static float getYawFromVector(Vector direction) {
        double dx = direction.getX();
        double dz = direction.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
        return yaw < 0.0F ? yaw + 360.0F : yaw;
    }

    public static float getPitchFromVector(Vector direction) {
        double dx = direction.getX();
        double dz = direction.getZ();
        float pitch = (float)Math.toDegrees(Math.atan2(-dx, dz));
        return pitch < 0.0F ? pitch + 360.0F : pitch;
    }

    public static void executeConsoleCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void executePlayerCommand(Player player, String command) {
        Bukkit.dispatchCommand(player, command);
    }

    public static void registerIncomingPluginMessageListener(String channel, PluginMessageListener listener) {
        Bukkit.getMessenger().registerIncomingPluginChannel(ApolloPlugin.getStaticInstance(), channel, listener);
    }

    public static void registerOutgoingPluginMessageListener(String channel) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(ApolloPlugin.getStaticInstance(), channel);
    }

    public static void sendPluginMessage(String channel, byte[] message) {
        Bukkit.getServer().sendPluginMessage(ApolloPlugin.getStaticInstance(), channel, message);
    }

    public static void sendPluginMessage(Player player, String channel, byte[] message) {
        player.sendPluginMessage(ApolloPlugin.getStaticInstance(), channel, message);
    }

    public static NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(ApolloPlugin.getStaticInstance(), key);
    }

    public static String[] locationToStringArray(Location location) {
        return new String[] {
                location.getWorld().getName(),
                String.valueOf(location.getBlockX()),
                String.valueOf(location.getBlockY()),
                String.valueOf(location.getBlockZ())
        };
    }

}
