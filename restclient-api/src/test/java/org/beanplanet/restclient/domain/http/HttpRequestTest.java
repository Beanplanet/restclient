package org.beanplanet.restclient.domain.http;

import org.beanplanet.testing.utils.BeanTestSupport;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link HttpRequest}.
 *
 * @author Gary Watson
 */
public class HttpRequestTest {
    @Test
    public void properties() {
        new BeanTestSupport(new HttpRequest())
                .withMockitoValuesGenerator()
                .testProperties()
                .testBuilderProperties();
    }

    @Test
    public void getRequestUriWithBaseUri() throws Exception {
        assertThat(new HttpRequest().withBaseUri("http://hostname:1234/p1/p2").getRequestUri(), is(new URI("http://hostname:1234/p1/p2")));
    }

    @Test
    public void getRequestUriWithBaseUriOverriddenWithSpecificSchemeHostnameAndPort() throws Exception {
        assertThat(new HttpRequest().withBaseUri("http://hostname:123/p1/p2")
                                    .withScheme("otherScheme")
                                    .withHostname("otherHostname")
                                    .withPort(456)
                                    .getRequestUri(),
                   is(new URI("otherScheme://otherHostname:456/p1/p2")));
    }

    @Test
    public void getRequestUriWithBaseUriPathIsJoinedWithSpecificPath() throws Exception {
        assertThat(new HttpRequest().withBaseUri("http://hostname/p1/p2")
                                    .withPath("/p3/p4")
                                    .getRequestUri(),
                   is(new URI("http://hostname/p1/p2/p3/p4")));
    }

    @Test
    public void getRequestUriWithBaseUriQueryParamsAreJoinedWithSpecificQueryParams() throws Exception {
        assertThat(new HttpRequest().withBaseUri("http://hostname/p1/p2?q1=q1Value&q2=q2Value")
                                    .withQueryParam("q3", "q3Value")
                                    .withQueryParam("q4", "q4Value")
                                    .getRequestUri(),
                   is(new URI("http://hostname/p1/p2?q1=q1Value&q2=q2Value&q3=q3Value&q4=q4Value")));
    }

    @Test
    public void getRequestUriCanIncludePathParametersWhichAreEncodedCorrectly() throws Exception {
        assertThat(new HttpRequest().withBaseUri("http://hostname/p1/p2/{plainParamPath}/p4/{pathParamWithUri}/p6/{pathParamWithSpace}")
                                    .withPathParam("plainParamPath", "p3")
                                    .withPathParam("pathParamWithUri", "https://otherhost/123&q=xyz")
                                    .withPathParam("pathParamWithSpace", "p7 with spaces")
                                    .getRequestUri(),
                   is(new URI("http://hostname/p1/p2/p3/p4/https%3A%2F%2Fotherhost%2F123%26q%3Dxyz/p6/p7+with+spaces")));
    }
}