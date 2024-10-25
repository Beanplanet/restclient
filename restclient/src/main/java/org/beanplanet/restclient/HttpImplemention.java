package org.beanplanet.restclient;

import org.beanplanet.core.net.http.HttpRequest;
import org.beanplanet.core.net.http.HttpResponse;

/**
 * A Service Provider Interface (SPI) for the underlying HTTP Implementations.
 */
public interface HttpImplemention {
    /**
     * Executes the given HTTP request synchronously, returning the response.
     *
     * @param request the HTTP request to be executed.
     * @return the HTTP response, returned by the server.
     */
    HttpResponse execute(HttpRequest request);
}
