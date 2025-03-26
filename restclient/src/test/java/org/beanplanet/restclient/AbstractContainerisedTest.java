package org.beanplanet.restclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbstractContainerisedTest {
    static final protected GenericContainer<?> httpbin = new GenericContainer<>("kennethreitz/httpbin:latest")
            .withExposedPorts(80);

    static final protected WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());

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
        wireMockServer.start();
    }

    @BeforeAll
    static void beforeAllTests() {
        clientBuilder = RestClient.builder();
        client = clientBuilder.build();
        wireMockServer.resetAll();
    }
}
