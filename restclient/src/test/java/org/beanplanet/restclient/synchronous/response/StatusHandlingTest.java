package org.beanplanet.restclient.synchronous.response;

import org.beanplanet.core.net.http.HttpStatusCode;
import org.beanplanet.restclient.AbstractRestClientTest;
import org.beanplanet.restclient.RestErrorResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class StatusHandlingTest extends AbstractRestClientTest {
    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andExceptionFactory_whenTheCodeMatchesTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(status, () -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andResponseExceptionFactory_whenTheCodeMatchesTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(status, res -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andBiFunctionExceptionFactory_whenTheCodeMatchesTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(status, (req, res) -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCodePredicate_andExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(statusCode -> true, () -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCodePredicate_andResponseExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(statusCode -> true, res -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));

    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCodePredicate_andBiFunctionExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + status)
                              .execute()
                              .onStatusCodeThrow(statusCode -> true, (req, res) -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));

    }

    @Test
    void givenARequestAndSuccessHandlerForStatusCode_whenBodyOn2xxSuccessful_thenTheHandlerIsCalledSuccessfully() {
        String body = client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/200")
                            .execute()
                            .onStatusCodeNotThenThrow(HttpStatusCode::is2xxSuccessful, () -> new RuntimeException("Failed"))
                            .bodyOn2xxSuccessful(String.class);
        assertThat(body, equalTo(""));
    }

    @Test
    void givenARequestAndErrorHandlerForStatusCode_whenOnStatusCodeNotThenThrow_thenTheHandlerIsCalledSuccessfully() {
        RuntimeException ex = assertThrows("Status code handler was not called for non 2xx response status",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
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
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .bodyOn2xxSuccessful(String.class)
        );
    }

    @Test
    void givenAnOnStatusPredicate_andBiFunctionExceptionFactory_whenThePredicateAnswersTrue_thenTheConfiguredExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code handler was not called for response status",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onStatusThrow(rs -> true, (req, res) -> new RuntimeException("Error occurred"))
        );
        assertThat(ex.getMessage(), containsString("Error occurred"));
    }

    @Test
    void givenAnOnStatusPredicate_andResponseFunctionExceptionFactory_whenThePredicateAnswersTrue_thenTheConfiguredExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code handler was not called for response status",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onStatusThrow(rs -> true, res -> new RuntimeException("Error occurred"))
        );
        assertThat(ex.getMessage(), containsString("Error occurred"));
    }

    @Test
    void givenAnOnStatusPredicate_andExceptionFactory_whenThePredicateAnswersTrue_thenTheConfiguredExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code handler was not called for response status",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onStatusThrow(rs -> true, () -> new RuntimeException("Error occurred"))
        );
        assertThat(ex.getMessage(), containsString("Error occurred"));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andExceptionFactory_whenTheCodeDoesMatchTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusCodeNotThenThrow(status, () -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andResponseExceptionFactory_whenTheCodeDoesMatchTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusCodeNotThenThrow(status, res -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAStatusCode_andBiFunctionExceptionFactory_whenTheCodeDoesMatchTheResponseStatusCode_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusCodeNotThenThrow(status, (req, res) -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAResponseStatusNotPredicate_andExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusNotThenThrow(resStatus -> false, () -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAResponseStatusNotPredicate_andResponseExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusNotThenThrow(resStatus -> false, res -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 202, 300, 400, 500})
    void givenAResponseStatusNotPredicate_andBiFunctionExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown(int status) {
        RuntimeException ex = assertThrows("Status code not handler was not called for HTTP response status " + status,
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/" + (status + 1))
                              .execute()
                              .onStatusNotThenThrow(resStatus -> false, (req, res) -> new RuntimeException("Error for status " + status))
        );
        assertThat(ex.getMessage(), containsString("Error for status " + status));
    }

    @Test
    void givenAResponsePredicate_andExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseThrow(res -> true, () -> new RuntimeException("Error for status"))
        );
        assertThat(ex.getMessage(), containsString("Error for status"));
    }

    @Test
    void givenAResponsePredicate_andResponseExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseThrow(res -> true, res -> new RuntimeException("Error for status"))
        );
        assertThat(ex.getMessage(), containsString("Error for status"));
    }

    @Test
    void givenAResponsePredicate_andBiFunctionExceptionFactory_whenThePredicateAnswersTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseThrow(res -> true, (req, res) -> new RuntimeException("Error for status"))
        );
        assertThat(ex.getMessage(), containsString("Error for status"));
    }

    @Test
    void givenAResponseNotPredicate_andExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseNotThenThrow(res -> false, () -> new RuntimeException("Error not for status"))
        );
        assertThat(ex.getMessage(), containsString("Error not for status"));
    }

    @Test
    void givenAResponseNotPredicate_andResponseExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseNotThenThrow(res -> false, res -> new RuntimeException("Error not for status"))
        );
        assertThat(ex.getMessage(), containsString("Error not for status"));
    }

    @Test
    void givenAResponseNotPredicate_andBiFunctionExceptionFactory_whenThePredicateDoesNotAnswerTrue_thenTheExceptionIsThrown() {
        RuntimeException ex = assertThrows("Status code not handler was not called for response ",
                RuntimeException.class, () ->
                        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
                              .execute()
                              .onResponseNotThenThrow(res -> false, (req, res) -> new RuntimeException("Error not for status"))
        );
        assertThat(ex.getMessage(), containsString("Error not for status"));
    }


    @Test
    void givenAResponse_andResponsePeek_whenInvoked_thenThePeekCallbackIsSuccessfullyInvoked() {
        final boolean[] peeked = new boolean[]{false};
        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
              .execute()
              .peek(res -> peeked[0] = true)
              .response();
        assertThat(peeked[0], is(true));
    }

    @Test
    void givenAResponse_andBiFunctionPeek_whenInvoked_thenThePeekCallbackIsSuccessfullyInvoked() {
        final boolean[] peeked = new boolean[]{false};
        client.get("http://localhost:" + httpbin.getFirstMappedPort() + "/status/400")
              .execute()
              .peek((req, res) -> peeked[0] = true)
              .response();
        assertThat(peeked[0], is(true));
    }
}
