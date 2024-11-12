package org.beanplanet.restclient.synchronous.request.body;

import com.github.dockerjava.transport.DockerHttpClient;
import org.beanplanet.core.net.http.HttpHeaders;
import org.beanplanet.core.net.http.MediaTypes;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ByteArrayBodyTest extends AbstractContainerisedTest {
    @Test
    void givenARequest_whenSent_thenADefaultContentTypeIsSet_andTheRequestIsSentCorrectly() {
        HttpBinAnythingResponse res = clientBuilder.build()
                                                   .post("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .body("Hello World!".getBytes())
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getMethod(), equalTo(DockerHttpClient.Request.Method.POST.name()));
        assertThat(res.getHeaders().get(HttpHeaders.CONTENT_TYPE), equalTo(MediaTypes.Application.OCTET_STREAM.getCanonicalForm()));
        assertThat(res.getData(), equalTo("Hello World!"));
    }

    @Test
    void givenARequest_andExplicitContentType_whenSent_thenNoDefaultContentTypeIsApplied_andTheRequestIsSentCorrectly() {
        HttpBinAnythingResponse res = clientBuilder.build()
                                                   .post("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .contentType(MediaTypes.Text.HTML)
                                                   .body("Hello World!".getBytes())
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getMethod(), equalTo(DockerHttpClient.Request.Method.POST.name()));
        assertThat(res.getHeaders().get(HttpHeaders.CONTENT_TYPE), equalTo(MediaTypes.Text.HTML.getCanonicalForm()));
        assertThat(res.getData(), equalTo("Hello World!"));
    }
}
