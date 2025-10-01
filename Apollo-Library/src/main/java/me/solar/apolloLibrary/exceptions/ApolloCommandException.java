package me.solar.apolloLibrary.exceptions;

public class ApolloCommandException extends RuntimeException {
    public ApolloCommandException(String message) {
        super(message);
    }

    public ApolloCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
