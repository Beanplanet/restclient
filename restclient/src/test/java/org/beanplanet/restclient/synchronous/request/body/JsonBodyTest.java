package org.beanplanet.restclient.synchronous.request.body;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.beanplanet.core.net.http.MediaTypes;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class JsonBodyTest extends AbstractContainerisedTest {
    @Test
    void givenARequestWithPojoBody_whenSentAsJsonMediaType_thenThePojoIsConvertedToJson_andSentCorrectly() {
        HttpBinAnythingResponse res = clientBuilder.build()
                                                   .post("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .contentType(MediaTypes.Application.JSON)
                                                   .body(new HelloWorld("Hello World!"))
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getData(), equalTo("{\"greeting\":\"Hello World!\"}"));
    }

    @Test
    void givenARequestWithPojoBody_andUnsupportedSerialiserFieldType_whenSentAsJsonMediaType_thenAnErrorOccurs_andA4xxResponseIsReceived() {
        RuntimeException ex = assertThrows("Jackson should have failed to serialise Instant type", RuntimeException.class, () ->
                clientBuilder.build()
                             .post("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                             .contentType(MediaTypes.Application.JSON)
                             .body(new HelloWorldWithSerialiserError(Instant.now()))
                             .execute()
                             .body(HttpBinAnythingResponse.class)
        );
        assertThat(ex.getMessage(), containsString("not supported by default"));
    }

    @RequiredArgsConstructor
    @Getter
    private static class HelloWorld {
        private final String greeting;
    }

    @RequiredArgsConstructor
    @Getter
    private static class HelloWorldWithSerialiserError {
        private final Instant date;
    }
}
