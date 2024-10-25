package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.core.net.http.Cookie;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CookieTest extends AbstractContainerisedTest {
    @Test
    void givenARequestProtoCookie_whenSent_thenTheHeadersAreCorrectlySentWitHTheRequest() {
        final Cookie cookie = new Cookie("aRequestProtoCookieName", "aRequestProtoCookieValue");
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.cookie(cookie))
                                                   .build()
                                                   .request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything"))
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        assertThat(res.getHttpHeaders().get(Cookie.HTTP_REQUEST_HEADER_NAME), equalTo(cookie.toHttpRequestHeaderValue()));
    }

    @Test
    void givenARequestProtoCookie_andSpecificCookie_whenSent_thenTheHeadersAreCorrectlySentWitHTheRequest() {
        final Cookie protoCookie = new Cookie("aRequestProtoCookieName", "aRequestProtoCookieValue");
        final Cookie cookie = new Cookie("aRequestCookieName", "aRequestCookieValue");
        HttpBinAnythingResponse res = clientBuilder.requestPrototype(r -> r.cookie(protoCookie))
                                                   .build()
                                                   .request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
                                                           .cookie(cookie)
                                                   )
                                                   .execute()
                                                   .body(HttpBinAnythingResponse.class);
        // Multiple values delimited by semi-colon (;) as cookie attributes can be comma-separated, already
        assertThat(res.getHttpHeaders().get(Cookie.HTTP_REQUEST_HEADER_NAME), equalTo(protoCookie.toHttpRequestHeaderValue()+"; "+cookie.toHttpRequestHeaderValue()));
    }
}
