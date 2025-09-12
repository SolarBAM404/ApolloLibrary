package me.solarbam.apollo.apolloCore.utils;

import me.solarbam.apollo.apolloCore.Common;

public final class Valid {

    private Valid() {
        // Utility class
    }

    public static void checkBoolean(boolean value, String message) {
        if (!value) {
            Common.throwError(new IllegalArgumentException(message), "A boolean check failed: " + message);
        }
    }

    public static void checkNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkNotEmpty(String value) {
        checkNotNull(value, "Value cannot be null");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }
}

