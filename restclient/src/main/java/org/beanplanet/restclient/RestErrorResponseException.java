package org.beanplanet.restclient;

/**
 * An exception thrown when a given HTTP response is unacceptable. By default, if not explicitly handled, HTTP responses
 * in the 4xx and 5xx ranges will lead to this exception.
 */
public class RestErrorResponseException extends RuntimeException {
    /**
     * Constructs a new REST response exception.
     *
     * @param requestUri the URI of the resource specified in the HTTP request, which led to this exception.
     * @param statusCode the HTTP response status code returned for the request.
     */
    public RestErrorResponseException(final String requestUri, final int statusCode) {
        super("HTTP response status ["+statusCode+"] for URI ["+requestUri+"]");
    }
}
