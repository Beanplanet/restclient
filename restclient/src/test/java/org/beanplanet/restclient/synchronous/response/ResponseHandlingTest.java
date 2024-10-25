package org.beanplanet.restclient.synchronous.response;

import org.beanplanet.core.net.http.HttpStatusCode;
import org.beanplanet.restclient.AbstractRestClientTest;
import org.beanplanet.restclient.RestErrorResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class ResponseHandlingTest extends AbstractRestClientTest {
    @ParameterizedTest
    @ValueSource(ints = {100, 201, 202, 300, 400, 500})
    void givenARequestAndAnExceptionHandlerForStatusCode_whenAResponseIsReceivedWithTheHandlerStatusCode_thenTheHandlerIsCalledSuccessfully(int status) {
        assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status))
                              .execute()
                              .onStatusCodeThrow(status, () -> new RuntimeException("HTTP status code handler was called for " + status))
        );
    }

    @Test
    void givenARequestAndSuccessHandlerForStatusCode_whenBodyOn2xxSuccessful_thenTheHandlerIsCalledSuccessfully() {
        String body = client.request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/200"))
                            .execute()
                            .onStatusCodeNotThenThrow(HttpStatusCode::is2xxSuccessful, () -> new RuntimeException("Failed"))
                            .bodyOn2xxSuccessful(String.class);
        assertThat(body, equalTo(""));
    }

    @Test
    void givenARequestAndErrorHandlerForStatusCode_whenOnStatusCodeNotThenThrow_thenTheHandlerIsCalledSuccessfully() {
        RuntimeException ex = assertThrows("Status code handler was not called for non 2xx response status",
                RuntimeException.class, () ->
                        client.request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400"))
                              .execute()
                              .onStatusCodeNotThenThrow(HttpStatusCode::is2xxSuccessful, () -> new RuntimeException("HTTP status code handler was called when status code not 2xx"))
                              .bodyOn2xxSuccessful(String.class)
        );
        assertThat(ex.getMessage(), containsString("status code handler was called when status code not 2xx"));
    }

    @Test
    void givenARequestAndNoErrorHandlerForStatusCode_whenResponseIsReceivedAndNoHandlerHasBeenRegistered_thenTheDefaultHandlerThrowsAnException() {
        assertThrows("Status code handler was not called for non 2xx response status",
                RestErrorResponseException.class, () ->
                        client.request(r -> r.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400"))
                              .execute()
                              .bodyOn2xxSuccessful(String.class)
        );
    }
}
