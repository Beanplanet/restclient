package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeaderTest extends AbstractContainerisedTest {
    @Test
    void givenARequestProtoTypeWithHeaders_whenSent_thenTheHeadersAreCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.header("aRequestProtoHeader", "theRequestProtoHeaderValue"))
                                                   .build()
                                                   .get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("aRequestProtoHeader").get(), equalTo("theRequestProtoHeaderValue"));
    }

    @Test
    void givenARequestWithHeaders_andSpecificRequestHeaders_whenSent_thenTheHeadersAreCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.header("aRequestProtoHeader", "theRequestProtoHeaderValue"))
                                                   .build()
                                                   .get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .header("aRequestHeader", "theRequestHeaderValue")
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("aRequestProtoHeader").get(), equalTo("theRequestProtoHeaderValue"));
        assertThat(res.getHttpHeaders().get("aRequestHeader").get(), equalTo("theRequestHeaderValue"));
    }

    @Test
    void givenARequestProtoTypeWithHeader_andSpecificSameHeader_whenSent_thenTheHeadersAreCorrectlySentWitHTheRequest() {
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.header("aRequestHeader", "theRequestProtoHeaderValue"))
                                                   .build()
                                                   .get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                   .header("aRequestHeader", "theSpecificRequestHeaderValue")
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get("aRequestHeader").get(), equalTo("theRequestProtoHeaderValue,theSpecificRequestHeaderValue"));
    }
}
