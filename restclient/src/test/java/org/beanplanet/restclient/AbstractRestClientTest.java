package org.beanplanet.restclient;

import org.junit.jupiter.api.BeforeAll;

public class AbstractRestClientTest extends AbstractContainerisedTest {
    protected static RestClient client;

    @BeforeAll
    static void beforeAllTests() {
        client = RestClient.builder().build();
    }
}
