package me.solar.apolloLibrary.exceptions;

/**
 * Exception thrown when a plugin-related error occurs.
 */
public class PluginException extends RuntimeException {
    /**
     * Constructs a PluginException with a message.
     * @param message the exception message
     */
    public PluginException(String message) {
        super(message);
    }
}
