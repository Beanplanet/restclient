package org.beanplanet.restclient.service;

import org.beanplanet.restclient.domain.http.HttpRequest;
import org.apache.commons.collections4.Predicate;

/**
 * Definition of a REST service, supporting all the common HTTP methods.
 *
 * @author Gary Watson
 * @author Geoffrey Major
 */
public interface RestService {
    /**
     * Configure the service for requests matching the given predicate.
     *
     * @param requestMatcher a matcher of requests to configuration.
     * @param configuration the configuration which will be applied to requests matching the given request matcher.
     * @return the rest service for invocation chaining.
     */
    RestService config(Predicate<HttpRequest> requestMatcher, Configuration configuration);

    /**
     * Gets the default configuration which will be applied to requests not accepted by any other request matcher.
     *
     * @return the default configuration which will be applied to requests not accepted by any other request matcher.
     */
    Configuration config();

    /**
     * Configure the default configuration which will be applied to requests not accepted by any other request matcher. This is equivalent to a call
     * <code>configure(true, Configuration)</code>, matching all requests after all other matchers have first been given the opportunity to match first.
     *
     * @param configuration the default configuration which will be applied to requests not accepted by any other request matcher.
     * @return the rest service for invocation chaining.
     */
    RestService config(Configuration configuration);

    RestBuilder createRestBuilder();
}
