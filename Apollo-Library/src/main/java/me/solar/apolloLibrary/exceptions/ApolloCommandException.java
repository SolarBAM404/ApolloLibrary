package me.solar.apolloLibrary.exceptions;

/**
 * Exception thrown when an Apollo command fails.
 */
public class ApolloCommandException extends RuntimeException {
    /**
     * Constructs an ApolloCommandException with a message.
     * @param message the exception message
     */
    public ApolloCommandException(String message) {
        super(message);
    }

    /**
     * Constructs an ApolloCommandException with a message and cause.
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public ApolloCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
