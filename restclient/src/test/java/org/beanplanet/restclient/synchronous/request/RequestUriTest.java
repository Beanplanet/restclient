package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.core.net.UriUtil;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestUriTest extends AbstractContainerisedTest {
    @ParameterizedTest
    @ValueSource(strings = {"/anything", "/anything/path1", "/anything/path1/path2/", "/get"})
    void givenAUriSpecifiedUsingTheRequestBuilder_whenARequestIsSent_thenTheCorrectUriIsSent(final String expectedPath) {
        HttpBinAnythingResponse body = client.get("http://localhost:" + httpbin.getFirstMappedPort() + expectedPath)
                                             .execute()
                                             .body(HttpBinAnythingResponse.class);
        assertThat(body.getUrl(), endsWith(expectedPath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/anything", "/anything/path1", "/anything/path1/path2/", "/get"})
    void givenABaseUriSpecifiedOnPrototypeWithNoPath_whenARequestUriPathIsSpecified_thenTheBaseAndUriPathsAreCombined(final String expectedPath) {
        HttpBinAnythingResponse body = clientBuilder.requestPrototype(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort()))
                                                    .build()
                                                    .uri(expectedPath)
                                                    .execute()
                                                    .body(HttpBinAnythingResponse.class);
        assertThat(body.getUrl(), endsWith(expectedPath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "path1", "path1/path2/", "get"})
    void givenABaseUriSpecifiedOnPrototypeWithPath_whenARelativeRequestUriPathIsSpecified_thenTheBaseAndUriPathsAreCombined(final String expectedPath) {
        HttpBinAnythingResponse body = clientBuilder.requestPrototype(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                    .build()
                                                    .uri(expectedPath)
                                                    .execute()
                                                    .body(HttpBinAnythingResponse.class);
        assertThat(body.getUrl(), endsWith(UriUtil.mergePaths("/anything", expectedPath)));
    }

    @Test
    void givenABaseUriSpecifiedOnPrototypeWithPath_whenAnAbsoluteRequestUriPathIsSpecified_thenTheBaseAndUriPathsAreCombined() {
        HttpBinAnythingResponse body = clientBuilder.requestPrototype(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                    .build()
                                                    .uri("/anything/overrides")
                                                    .execute()
                                                    .body(HttpBinAnythingResponse.class);
        assertThat(body.getUrl(), endsWith(UriUtil.mergePaths("/anything", "/anything/overrides")));
    }

    @Test
    void givenABaseUriSpecifiedOnPrototypeWithPath_whenAnAbsoluteRequestUriIsSpecified_thenTheBaseAndUriPathsAreCombined() {
        HttpBinAnythingResponse body = clientBuilder.requestPrototype(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                    .build()
                                                    .uri("http://127.0.0.1:" + httpbin.getFirstMappedPort() + "/anything/overrides")
                                                    .execute()
                                                    .body(HttpBinAnythingResponse.class);
        assertThat(body.getUrl(), endsWith(UriUtil.mergePaths("/anything", "/anything/overrides")));
    }
}
