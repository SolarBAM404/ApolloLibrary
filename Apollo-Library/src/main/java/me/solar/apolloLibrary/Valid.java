package me.solar.apolloLibrary;

public class Valid {

    private Valid() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static void checkBoolean(boolean bool, String message) {
        if (!bool) {
            throw new IllegalArgumentException(message);
        }
    }

}
