package org.beanplanet.restclient.httpclient.integrationtests;

import org.beanplanet.restclient.httpclient.service.HttpClientRestService;
import org.junit.Test;

import static org.beanplanet.restclient.httpclient.service.HttpConfiguration.httpConfig;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetMethodTest {
    @Test
    public void getWithStringEntity() throws Exception {
        // Given
        HttpClientRestService service = new HttpClientRestService();
        service.initialise();

        // When
        String body = service
                .createRestBuilder()
                .baseUri("https://www.google.co.uk")
                .get(String.class);

        // Then
        assertThat(body, containsString("<html"));
    }
}
