package org.beanplanet.restclient;

/**
 * The superclass of all REST Client exceptions.
 */
public class RestClientException extends RuntimeException {
    /**
     * Constructs a new REST exception.
     *
     * @param message the message describing the cause.
     * @param cause the underlying cause of the exception.
     */
    public RestClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new REST exception.
     *
     * @param message the message describing the cause.
     */
    public RestClientException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new REST exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public RestClientException(final Throwable cause) {
        super(cause);
    }
}
