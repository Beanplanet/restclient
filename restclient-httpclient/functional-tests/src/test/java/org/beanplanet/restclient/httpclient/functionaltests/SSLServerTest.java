package org.beanplanet.restclient.httpclient.functionaltests;

import org.beanplanet.restclient.httpclient.service.HttpClientRestService;
import org.beanplanet.restclient.service.RestResponse;
import org.beanplanet.restclient.service.RestService;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import javax.net.ssl.SSLHandshakeException;

import static org.beanplanet.restclient.httpclient.service.HttpConfiguration.httpConfig;
import static org.beanplanet.restclient.httpclient.service.HttpTlsConfiguration.tlsConfig;
import static org.beanplanet.restclient.service.RequestMatchers.host;
import static org.beanplanet.restclient.service.RequestMatchers.hosts;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class SSLServerTest {
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
    public void sslServiceWithUntrustedHost() {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        try {
            restService
                    .createRestBuilder()
                    .baseUri("https://52.17.2.201:8030/logdata")
                    .get(RestResponse::getStatusCode);
            fail("The connection to an untrusted host should have failed.");
        } catch (RuntimeException rtEx) {
            assertThat(rtEx.getCause(), instanceOf(SSLHandshakeException.class));
        }
    }

    @Test
    public void trustedSslServiceWithTrustStoreConfigured() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig().trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")));
        restService.initialise();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://52.17.2.201:8030/logdata")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(405));
    }

    @Test
    public void trustedSslServiceWithHostMatchedTrustStoreConfigured() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(host("52.17.2.201"), httpConfig(tlsConfig().trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")));
        restService.initialise();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://52.17.2.201:8030/logdata")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(405));
    }

    @Test
    public void trustedMutualSsl() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("d:\\etc\\bbcww\\storeid-client.p12"), "PKCS12", "abc123")
                                  )
                       );
        restService.initialise();

        // When
        int httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://internal-api-dev.store.bbc.com/v1/status")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));
    }

    @Test
    public void trustedMutualSslWithResponse() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("d:\\etc\\bbcww\\storeid-client.p12"), "PKCS12", "abc123")
                                  )
                       );
        restService.initialise();

        // When
        try (RestResponse restResponse = restService
                .createRestBuilder()
                .baseUri("https://internal-api-dev.store.bbc.com/v1/status")
                .getResponse()) {
            // Then
            assertThat(restResponse.getStatusCode(), is(200));
        }
    }

    @Test
    public void trustedMutualHostmatchedSsl() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(hosts("internal-api-dev.store.*", "internal-api-test.store.bbc.com"),
                        httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("d:\\etc\\bbcww\\storeid-client.p12"), "PKCS12", "abc123")
                                  )
                       )
                .config(host(".*-preprod.*"),
                        httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("D:\\Dev\\bbcww\\projects\\2016\\2016-07-25-BBCStoreId-Client Certs\\preprod.p12"), "PKCS12", "ch4nn3l8")
                                  )
                       )
                .config(host("internal-api-prod.store.bbc.com"),
                        httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("D:\\Dev\\bbcww\\projects\\2016\\2016-07-25-BBCStoreId-Client Certs\\prod.p12"), "PKCS12", "ch4nn3l8")
                                  )
                       );
        restService.initialise();

        // When (dev)
        int httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://internal-api-dev.store.bbc.com/v1/status")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));

        // When (test)
        httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://internal-api-test.store.bbc.com/v1/status")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));

        // When (preprod)
        httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://internal-api-preprod.store.bbc.com/v1/status")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));

        // When (prod)
        httpStatusCode = restService
                .createRestBuilder()
                .baseUri("https://internal-api-prod.store.bbc.com/v1/status")
                .get(RestResponse::getStatusCode);

        // Then
        assertThat(httpStatusCode, is(200));
    }
}
