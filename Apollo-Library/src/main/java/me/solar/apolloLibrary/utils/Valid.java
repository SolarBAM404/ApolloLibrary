package me.solar.apolloLibrary.utils;

/**
 * Utility class for validation checks.
 */
public class Valid {

    /**
     * Private constructor to prevent instantiation.
     */
    private Valid() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the given object is null.
     *
     * @param object the object to check
     * @return true if the object is null, false otherwise
     */
    public static boolean isNull(Object object) {
        System.out.println(object);
        return object == null;
    }

    /**
     * Checks if the given object is not null.
     *
     * @param object the object to check
     * @return true if the object is not null, false otherwise
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static void checkNull(Object object, String message) {
        if (!isNull(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotNull(Object object) {
        if (!isNotNull(object)) {
            throw new IllegalArgumentException("Object cannot be null");
        }
    }

    public static void checkNotNull(Object object, String message) {
        if (!isNotNull(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNull(Object object) {
        if (!isNull(object)) {
            throw new IllegalArgumentException("Object cannot be null");
        }
    }

    /**
     * Throws an IllegalArgumentException with the given message if the boolean is false.
     *
     * @param bool the boolean to check
     * @param message the exception message if the check fails
     */
    public static void checkBoolean(boolean bool, String message) {
        if (!bool) {
            throw new IllegalArgumentException(message);
        }
    }

}
