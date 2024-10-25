package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.core.net.http.MediaTypes;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContentTypeTest extends AbstractContainerisedTest {
    @Test
    void givenARequestProtoMediaType_whenSent_thenTheContentTypeIsCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.contentType(MediaTypes.Text.XML))
                                                   .build()
                                                   .request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("Content-Type"), equalTo(MediaTypes.Text.XML.getName()));
    }

    @Test
    void givenARequestProtoContentType_whenSent_thenTheContentTypeIsCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.contentType(MediaTypes.Image.SVG.getCanonicalForm()))
                                                   .build()
                                                   .request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("Content-Type"), equalTo(MediaTypes.Image.SVG.getName()));
    }

    @Test
    void givenARequestMediaType_whenSent_thenTheContentTypeIsCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.build()
                                                   .request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                                  .contentType(MediaTypes.Text.XML)
                                                   )
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("Content-Type"), equalTo(MediaTypes.Text.XML.getName()));
    }
}
