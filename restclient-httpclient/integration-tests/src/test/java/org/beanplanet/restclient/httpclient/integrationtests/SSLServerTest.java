package org.beanplanet.restclient.httpclient.integrationtests;

import org.beanplanet.restclient.httpclient.service.HttpClientRestService;
import org.beanplanet.restclient.service.RestResponse;
import org.beanplanet.restclient.service.RestService;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.SSLHandshakeException;

import static org.beanplanet.restclient.httpclient.service.HttpConfiguration.httpConfig;
import static org.beanplanet.restclient.httpclient.service.HttpTlsConfiguration.tlsConfig;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class SSLServerTest {
    @Rule
    public WireMockRule wm = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    @Test
    public void httpsCallUsesSystemDefaultSslConfiguration() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService();
        restService.initialise();

        // When
        int httpResponseStatusCode =
            restService
                    .createRestBuilder()
                    .baseUri("https://www.google.co.uk")
                    .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpResponseStatusCode, is(200));
    }

    @Test
    public void untrustedHost() {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        try {
            restService
                    .createRestBuilder()
                    .baseUri(wm.url(""))
                    .get(RestResponse::getStatusCode);
            fail("The connection to an untrusted host should have failed.");
        } catch (RuntimeException rtEx) {
            assertThat(rtEx.getCause(), instanceOf(SSLHandshakeException.class));
        }
    }

    @Test
    public void trustedHost() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig().trustStore(new UrlResource(wm.getOptions().httpsSettings().keyStorePath()),
                                                          wm.getOptions().httpsSettings().keyStoreType(), wm.getOptions().httpsSettings().keyStorePassword())));
        restService.initialise();

        // When
        restService
                .createRestBuilder()
                .baseUri(wm.url(""))
                .get(RestResponse::getStatusCode);
    }
}
