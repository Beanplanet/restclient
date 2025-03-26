package org.beanplanet.restclient.synchronous.response.body;

import org.beanplanet.core.net.http.HttpHeaders;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.beanplanet.core.net.http.MediaTypes.Application.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonBodyTest extends AbstractContainerisedTest {
    @Test
    void givenAResponseWithJsonMediaTypeBody_whenTheBodyIsRequestedAsMap_thenTheJsonIsDeserialisedCorrectlyAsAMap() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/")).willReturn(aResponse().withStatus(200)
                                                                          .withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName())
                                                                          .withBody("{ \"a\": [1, 2, 3] }")));
        // When
        var map = client.get("http://localhost:" + wireMockServer.port() + "/")
                        .execute()
                        .body(Map.class);

        // Then
        assertThat(map, equalTo(Map.of("a", List.of(1, 2, 3))));
    }

    @Test
    void givenAResponseWithJsonMediaTypeAndCharsetBody_whenTheBodyIsRequestedAsMap_thenTheJsonIsDeserialisedCorrectlyAsAMap() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/")).willReturn(aResponse().withStatus(200)
                                                                          .withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()+"; charset=UTF-8")
                                                                          .withBody("{ \"a\": [1, 2, 3] }")));
        // When
        var map = client.get("http://localhost:" + wireMockServer.port() + "/")
                        .execute()
                        .body(Map.class);

        // Then
        assertThat(map, equalTo(Map.of("a", List.of(1, 2, 3))));
    }
}
