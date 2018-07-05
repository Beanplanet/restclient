package org.beanplanet.restclient.httpclient.functionaltests;

import org.beanplanet.restclient.httpclient.service.HttpClientRestService;
import org.beanplanet.restclient.service.RestResponse;
import org.beanplanet.restclient.service.RestService;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.util.Map;

import static org.beanplanet.restclient.httpclient.service.HttpConfiguration.httpConfig;
import static org.beanplanet.restclient.httpclient.service.HttpTlsConfiguration.tlsConfig;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetMethodTest {
    @Test
    public void getWithStringEntity() throws Exception {
        // Given
        HttpClientRestService service = new HttpClientRestService();
        service.initialise();

        // When
        String body = service
                .createRestBuilder()
                .baseUri("https://www.google.co.uk")
                .get(String.class);

        // Then
        assertThat(body, containsString("<html"));
    }
    @Test
    public void jsonCanBeConvertedToAPojoViaResponse() throws Exception {
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
                .baseUri("https://internal-api-dev.store.bbc.com")
                .header("Admin-Id", "StoreID-Tests")
                .header("X-Authentication-Provider", "iDv5")
                .accept("application/json")
                .path("/v1/admin/users/313ab661fc33f7d6f8a69f00d9839224d0101d485f7450409729d38a0d9ed0ace485760a73ed65066b25bb28c7cc0f79c3b2")
                .getResponse()) {
            // Then
            assertThat(restResponse.getStatusCode(), is(200));

            Map<String, Object> userProperties = restResponse.getEntity(Map.class);
            assertThat(userProperties, notNullValue());
        }
    }

    public void jsonCanBeConvertedToAPojoDeclaritively() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("d:\\etc\\bbcww\\storeid-client.p12"), "PKCS12", "abc123")
                                  )
                       );
        restService.initialise();

        // When
        Map mapOfProperties  = restService
                .createRestBuilder()
                .baseUri("https://internal-api-dev.store.bbc.com")
                .header("Admin-Id", "StoreID-Tests")
                .header("X-Authentication-Provider", "iDv5")
                .accept("application/json")
                .path("/v1/admin/users/313ab661fc33f7d6f8a69f00d9839224d0101d485f7450409729d38a0d9ed0ace485760a73ed65066b25bb28c7cc0f79c3b2")
                .get(Map.class);

        // Then
        assertThat(mapOfProperties, notNullValue());
    }

    @Test(expected=RuntimeException.class)
    public void anExceptionIsThrownIfTheEntityCannotBeConvertedToAPojo() throws Exception {
        // Given
        RestService restService = new HttpClientRestService();

        // When
        restService
                .createRestBuilder()
                .baseUri("http://www.google.co.uk")
                .get(Integer.class);
    }

    @Test(expected = IllegalStateException.class)
    public void anExceptionIsThrownIfTheReturnedStatusCodeIsNotThatExpected() throws Exception {
        // Given
        HttpClientRestService restService = new HttpClientRestService()
                .config(httpConfig(tlsConfig()
                                           .trustStore(new FileSystemResource("d:\\etc\\bbcww\\bbcid-trust.jks"), "JKS", "changeit")
                                           .clientKeyStore(new FileSystemResource("d:\\etc\\bbcww\\storeid-client.p12"), "PKCS12", "abc123")
                                  )
                       );
        restService.initialise();

        // When
        restService
                .createRestBuilder()
                .baseUri("https://internal-api-dev.store.bbc.com")
                .header("Admin-Id", "StoreID-Tests")
                .header("X-Authentication-Provider", "iDv5")
                .accept("application/json")
                .path("/v1/admin/users/313ab661fc33f7d6f8a69f00d9839224d0101d485f7450409729d38a0d9ed0ace485760a73ed65066b25bb28c7cc0f79c3b2")
                .get(999, Integer.class);
    }


}
