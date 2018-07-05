package org.beanplanet.restclient.httpclient.functionaltests;

import org.beanplanet.restclient.httpclient.service.HttpClientRestService;
import org.beanplanet.restclient.service.RestResponse;
import org.beanplanet.restclient.service.RestService;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RedirectTest {
    @Test
    public void testFollowRedirectsOff() {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .followRedirects(false)
                .baseUri("http://www.google.com")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(302));
    }

    @Test
    public void testFollowRedirectsOn() {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .followRedirects(true)
                .baseUri("http://www.google.com")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));
    }

    @Test
    public void testFollowRedirectsDefaultIsOn() {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .baseUri("http://www.google.com")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));
    }
}
