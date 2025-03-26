package org.beanplanet.restclient.synchronous.response;

import org.beanplanet.core.net.http.HttpHeaders;
import org.beanplanet.restclient.AbstractRestClientTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.beanplanet.core.net.http.MediaTypes.Application.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParameterisedTypeBodyContentTest extends AbstractRestClientTest {
    @Test
    void givenAResponseWhichMayBeDeserialisedWithAParamewterisedListType_whenExecuted_thenTheResponseBodyIsReturnedCorrectly() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/echo")).willReturn(aResponse().withStatus(200)
                                                               .withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName())
                                                               .withBody("[1, 2, 3, 4, 5]")));

        List<Integer> response = client.get("http://localhost:" + wireMockServer.port() + "/echo")
                                       .execute()
                                       .bodyAsListOf(Integer.class);
        assertThat(response, equalTo(List.of(1, 2, 3, 4, 5)));
    }
}
