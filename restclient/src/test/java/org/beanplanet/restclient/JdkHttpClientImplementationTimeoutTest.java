package org.beanplanet.restclient;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The JDK implementation's optional request timeout, against a local server that answers slowly. */
class JdkHttpClientImplementationTimeoutTest {
    private static HttpServer server;
    private static URI slow;

    @BeforeAll
    static void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, 2);
            exchange.getResponseBody().write("ok".getBytes());
            exchange.close();
        });
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        slow = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/slow");
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    private static RestClient client(final Duration timeout) {
        return RestClient.builder()
                         .disableDefaultClientServerErrorResponseHandlers()
                         .httpImplementation(new JdkHttpClientImplementation(HttpClient.newHttpClient(), timeout))
                         .build();
    }

    @Test
    void aRequestTakingLongerThanTheTimeout_timesOut() {
        final RuntimeException e = assertThrows(RuntimeException.class, () -> client(Duration.ofMillis(200)).method("GET").uri(slow).execute());
        boolean timedOut = false;
        for (Throwable cause = e; cause != null; cause = cause.getCause()) timedOut |= cause instanceof HttpTimeoutException;
        assertTrue(timedOut, "expected an HttpTimeoutException among the causes of " + e);
    }

    @Test
    void withoutATimeout_theSlowResponseArrives() {
        assertThat(client(null).method("GET").uri(slow).execute().response().getStatusCode(), equalTo(200));
    }

    @Test
    void withALongerTimeout_theSlowResponseArrives() {
        assertThat(client(Duration.ofSeconds(10)).method("GET").uri(slow).execute().response().getStatusCode(), equalTo(200));
    }
}
