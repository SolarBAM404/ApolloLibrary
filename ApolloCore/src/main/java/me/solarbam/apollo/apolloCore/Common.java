package me.solarbam.apollo.apolloCore;

//import me.solarbam.apollo.apolloCore.settings.SimpleSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Common {
    private static final Pattern colourPatten = Pattern.compile("<[a-fA-F0-9]{6}>");

    public static BukkitAudiences adventure() {
        if (ApolloPlugin.getAdventure() == null)
            throw new IllegalStateException("Adventure is not initialized");
        return ApolloPlugin.getAdventure();
    }

    public static Component colorize(String string) {
        Component output = miniMessage(string);
        return output;
    }

    public static Component miniMessage(String string) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(string);
    }

    public static String stripColor(String string) {
        if (string.contains("<")) {
            return colourPatten.matcher(string).replaceAll("");
        }
        return string;
    }

    public static void tell(Player player, String message) {
        Audience audience = adventure().player(player);
        if (audience != null)
            audience.sendMessage(colorize(message));
    }

    public static void tellConsole(String message) {
        Audience audience = ApolloPlugin.getAdventure() != null ? ApolloPlugin.getAdventure().console() : null;
        if (audience == null) {
            System.out.println("Audience is null. Cannot send message to console: " + message);
            return;
        }
        audience.sendMessage(miniMessage(message));
    }

    public static void error(String message) {
        tellConsole("<red>" + message);
    }

    public static void error(Throwable throwable) {
        error(throwable.getMessage() != null ? throwable.getMessage() : "An error occurred");
        throwable.printStackTrace();
    }

    public static void line(Player player) {
        Audience audience = adventure().player(player);
        if (audience != null)
            audience.sendMessage(miniMessage("<gray>-----------------------------------------------------"));
    }

    public static void lineConsole() {
        @NotNull Audience audience = ApolloPlugin.getAdventure().console();
        if (audience == null) {
            System.out.println("Audience is null. Cannot send line message to console");
            return;
        }
        audience.sendMessage(miniMessage("<gray>-----------------------------------------------------"));
    }

    public static void box(Player player, String message) {
        Audience audience = adventure().player(player);
        if (audience == null) {
            tellConsole("Audience is null. Cannot send box to player: " + player.getName());
            return;
        }
        audience.sendMessage(miniMessage("<gray>-----------------------------------------------------"));
        audience.sendMessage(miniMessage("<gray>" + message));
        audience.sendMessage(miniMessage("<gray>-----------------------------------------------------"));
    }

    public static void throwError(Throwable t, String context) {
        error("An error occurred in " + context + ": " + (t.getMessage() != null ? t.getMessage() : "No message"));
        t.printStackTrace();
    }

    public static void log(String message) {
        tellConsole("<green>" + message);
    }

    /**
     * Formats the vector location to one digit decimal points
     *
     * DO NOT USE FOR SAVING, ONLY INTENDED FOR DEBUGGING
     * Use {@link SerializeUtil#serialize(Object)} to save a vector
     *
     * @param vec
     * @return
//     */
//    public static String shortLocation(final Vector vec) {
//        return " [" + MathUtil.formatOneDigit(vec.getX()) + ", " + MathUtil.formatOneDigit(vec.getY()) + ", " + MathUtil.formatOneDigit(vec.getZ()) + "]";
//    }
//
//    /**
//     * Formats the given location to block points without decimals
//     *
//     * DO NOT USE FOR SAVING, ONLY INTENDED FOR DEBUGGING
//     * Use {@link SerializeUtil#serialize(Object)} to save a location
//     *
//     * @param location
//     * @return
//     */
//    public static String shortLocation(final Location location) {
//        if (location == null)
//            return "Location(null)";
//
//        if (location.equals(new Location(null, 0, 0, 0)))
//            return "Location(null, 0, 0, 0)";
//
//        Valid.checkNotNull(location.getWorld(), "Cannot shorten a location with null world!");
//
//        return Replacer.replaceArray(SimpleSettings.LOCATION_FORMAT,
//                "world", location.getWorld().getName(),
//                "x", location.getBlockX(),
//                "y", location.getBlockY(),
//                "z", location.getBlockZ());
//    }
//
//    /**
//     * Replace some common classes such as entity to name automatically
//     *
//     * @param arg
//     * @return
//     */
//    public static String simplify(Object arg) {
//        if (arg instanceof Entity)
//            return Remain.getName((Entity) arg);
//
//        else if (arg instanceof CommandSender)
//            return ((CommandSender) arg).getName();
//
//        else if (arg instanceof World)
//            return ((World) arg).getName();
//
//        else if (arg instanceof Location)
//            return Common.shortLocation((Location) arg);
//
//        else if (arg.getClass() == double.class || arg.getClass() == float.class)
//            return MathUtil.formatTwoDigits((double) arg);
//
//        else if (arg instanceof Collection)
//            return Common.join((Collection<?>) arg, ", ", Common::simplify);
//
//        else if (arg instanceof ChatColor)
//            return ((Enum<?>) arg).name().toLowerCase();
//
//        else if (arg instanceof Enum)
//            return ((Enum<?>) arg).toString().toLowerCase();
//
//        try {
//            if (arg instanceof net.md_5.bungee.api.ChatColor)
//                return ((net.md_5.bungee.api.ChatColor) arg).getName();
//        } catch (final Exception e) {
//            // No MC compatible
//        }
//
//        return arg.toString();
//    }
//
//    /**
//     * A convenience method for converting array of objects into array of strings
//     * We invoke "toString" for each object given it is not null, or return "" if it is
//     *
//     * @param <T>
//     * @param array
//     * @return
//     */
//    public static <T> String join(final T[] array) {
//        return array == null ? "null" : join(Arrays.asList(array));
//    }
//
//    /**
//     * A convenience method for converting list of objects into array of strings
//     * We invoke "toString" for each object given it is not null, or return "" if it is
//     *
//     * @param <T>
//     * @param array
//     * @return
//     */
//    public static <T> String join(final Iterable<T> array) {
//        return array == null ? "null" : join(array, ", ");
//    }
//
//    /**
//     * A convenience method for converting list of objects into array of strings
//     * We invoke "toString" for each object given it is not null, or return "" if it is
//     *
//     * @param <T>
//     * @param array
//     * @param delimiter
//     * @return
//     */
//    public static <T> String join(final T[] array, final String delimiter) {
//        return join(array, delimiter, object -> object == null ? "" : simplify(object));
//    }
//
//    /**
//     * A convenience method for converting list of objects into array of strings
//     * We invoke "toString" for each object given it is not null, or return "" if it is
//     *
//     * @param <T>
//     * @param array
//     * @param delimiter
//     * @return
//     */
//    public static <T> String join(final Iterable<T> array, final String delimiter) {
//        return join(array, delimiter, object -> object == null ? "" : simplify(object));
//    }
//
//    /**
//     * Joins an array of a given type using the ", " delimiter and a helper interface
//     * to convert each element in the array into string
//     *
//     * @param <T>
//     * @param array
//     * @param stringer
//     * @return
//     */
//    public static <T> String join(final T[] array, final Stringer<T> stringer) {
//        return join(array, ", ", stringer);
//    }
//
//    /**
//     * Joins an array of a given type using the given delimiter and a helper interface
//     * to convert each element in the array into string
//     *
//     * @param <T>
//     * @param array
//     * @param delimiter
//     * @param stringer
//     * @return
//     */
//    public static <T> String join(final T[] array, final String delimiter, final Stringer<T> stringer) {
//        Valid.checkNotNull(array, "Cannot join null array!");
//
//        return join(Arrays.asList(array), delimiter, stringer);
//    }
//
//    /**
//     * Joins a list of a given type using the comma delimiter and a helper interface
//     * to convert each element in the array into string
//     *
//     * @param <T>
//     * @param array
//     * @param stringer
//     * @return
//     */
//    public static <T> String join(final Iterable<T> array, final Stringer<T> stringer) {
//        return join(array, ", ", stringer);
//    }
//
//    /**
//     * Joins a list of a given type using the given delimiter and a helper interface
//     * to convert each element in the array into string
//     *
//     * @param <T>
//     * @param array
//     * @param delimiter
//     * @param stringer
//     * @return
//     */
//    public static <T> String join(final Iterable<T> array, final String delimiter, final Stringer<T> stringer) {
//        final Iterator<T> it = array.iterator();
//        String message = "";
//
//        while (it.hasNext()) {
//            final T next = it.next();
//
//            if (next != null)
//                message += stringer.toString(next) + (it.hasNext() ? delimiter : "");
//        }
//
//        return message;
//    }
//
//    /**
//     * A simple interface from converting objects into strings
//     *
//     * @param <T>
//     */
//    public interface Stringer<T> {
//
//        /**
//         * Convert the given object into a string
//         *
//         * @param object
//         * @return
//         */
//        String toString(T object);
//    }
}

