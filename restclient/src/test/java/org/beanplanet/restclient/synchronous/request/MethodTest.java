package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.core.io.resource.ByteArrayResource;
import org.beanplanet.core.io.resource.DataUrlResource;
import org.beanplanet.core.net.http.MediaTypes;
import org.beanplanet.core.net.http.Request.Method;
import org.beanplanet.restclient.AbstractRestClientTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodTest extends AbstractRestClientTest {
    @Test
    void givenGetIsTheDefaultRequestMethod_whenARequestIsSentWithNoMethod_thenGetMethodIsSentAsDefault() {
        HttpBinAnythingResponse res = client.uri("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                            .execute()
                                            .body(HttpBinAnythingResponse.class);
        assertThat(res, notNullValue());
        assertThat(res.getMethod(), equalTo(Method.GET.name()));
        assertThat(res.getUrl(), endsWith("/anything"));
    }

    @ParameterizedTest
    @EnumSource(
            value = Method.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"CONNECT", "HEAD", "OPTIONS"})
    void givenACompleteRequest_whenSent_thenTheRequestDetailsAreSent_andResponseBodyIsReceivedSuccessfully(Method method) {
        final String requestBody = "Hello world!";
        HttpBinAnythingResponse res = client.method(method)
                                            .uri("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                            .body(requestBody)
                                            .execute()
                                            .body(HttpBinAnythingResponse.class);
        assertThat(res, notNullValue());
        assertThat(res.getMethod(), equalTo(method.name()));
        assertThat(res.getUrl(), endsWith("/anything"));
        assertThat(res.getData(), equalTo(requestBody));
    }

    @ParameterizedTest
    @EnumSource(
            value = Method.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"CONNECT", "HEAD", "OPTIONS"})
    void givenARequestAndBodyWithCharset_whenSent_thenTheRequestDetailsAreSent_andResponseBodyIsReceivedSuccessfully(Method method) {
        final String requestBody = "Hello world! £";
        HttpBinAnythingResponse res = client.method(method)
                                            .uri("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                            .body(
                                                    new ByteArrayResource(requestBody.getBytes(StandardCharsets.ISO_8859_1)),
                                                    MediaTypes.Text.PLAIN, StandardCharsets.ISO_8859_1
                                            )
                                            .execute()
                                            .body(HttpBinAnythingResponse.class);
        assertThat(res, notNullValue());
        assertThat(res.getMethod(), equalTo(method.name()));
        assertThat(res.getUrl(), endsWith("/anything"));
        assertThat(res.getData(), startsWith(DataUrlResource.DATA_URL_SCHEME_PROTOCOL));

        DataUrlResource dataUrlResource = new DataUrlResource(res.getData());
        assertThat(dataUrlResource.readFullyAsString(dataUrlResource.getCharset().orElse(StandardCharsets.ISO_8859_1)), equalTo(requestBody));
    }
}
