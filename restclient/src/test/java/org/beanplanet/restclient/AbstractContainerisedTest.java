package org.beanplanet.restclient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbstractContainerisedTest {
    static final protected GenericContainer<?> httpbin = new GenericContainer<>("kennethreitz/httpbin:latest")
            .withExposedPorts(80);

    protected static RestClient.RestClientBuilder clientBuilder;
    protected static RestClient client;

    /**
     * Uses the singleton containers pattern to only spin up containers once. Containers are spun up only once for use
     * by all subclasses and are decommissioned on JVM shutdown. This ensures fastest test execution time, but the
     * use of static startup is a bit ugly...
     *
     * See <a href="https://testcontainers.com/guides/testcontainers-container-lifecycle/#_using_singleton_containers">Using Singleton Containers</a>.
     */
    static {
        httpbin.start();
    }

    @BeforeAll
    static void beforeAllTests() {
        clientBuilder = RestClient.builder();
        client = clientBuilder.build();
    }
}
