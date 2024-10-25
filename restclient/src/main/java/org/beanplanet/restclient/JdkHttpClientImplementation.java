package org.beanplanet.restclient;

import org.beanplanet.core.io.IoException;
import org.beanplanet.core.io.IoUtil;
import org.beanplanet.core.io.resource.ByteArrayOutputStreamResource;
import org.beanplanet.core.models.Pair;
import org.beanplanet.core.net.http.HttpRequest;
import org.beanplanet.core.net.http.HttpResponse;
import org.beanplanet.core.net.http.Request;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

/**
 * A Http implementation using the standard JDK (11+) {@link java.net.http.HttpClient} to execute requests.
 */
public class JdkHttpClientImplementation implements HttpImplemention {
    private HttpClient client;

    public JdkHttpClientImplementation(final HttpClient client) {
        this.client = client;
    }

    public JdkHttpClientImplementation() {
        this(HttpClient.newBuilder()
                       .version(HttpClient.Version.HTTP_1_1)
                       .followRedirects(HttpClient.Redirect.NORMAL)
                       .build());
    }

    /**
     * Executes the given HTTP request synchronously, returning the response.
     *
     * @param request the HTTP request to be executed.
     * @return the HTTP response, returned by the server.
     */
    @Override
    public HttpResponse execute(HttpRequest request) {
        java.net.http.HttpRequest.Builder jdkRequestBuilder = java.net.http.HttpRequest.newBuilder();

        // URI
        jdkRequestBuilder.uri(request.getUri());

        // Version
        jdkRequestBuilder.version(toJdkHttpClientVersion(request.getHttpVersion()));

        for (Map.Entry<String, List<String>> headersByName : request.getHeaders().getAll().entrySet()) {
            for (String headerValue : headersByName.getValue()) {
                jdkRequestBuilder.header(headersByName.getKey(), headerValue);
            }
        }

        jdkRequestBuilder.method(request.getMethod(), request.getBody() == null ? java.net.http.HttpRequest.BodyPublishers.noBody() :  java.net.http.HttpRequest.BodyPublishers.ofInputStream(() -> request.getBody().getInputStream())); // toBodyHandler(request));

        java.net.http.HttpRequest jdkRequest = jdkRequestBuilder.build();

        try {
            ByteArrayOutputStreamResource responseBody = null;
            java.net.http.HttpResponse<InputStream> response = client.send(jdkRequest, java.net.http.HttpResponse.BodyHandlers.ofInputStream());
            InputStream bodyIs = response.body();

            if (bodyIs != null) {
                responseBody = new ByteArrayOutputStreamResource();
                IoUtil.transferAndClose(bodyIs, responseBody.getOutputStream());
            }

            return HttpResponse.builder()
                               .body(responseBody)
                               .statusCode(response.statusCode())
                               .headers(response.headers().map())
                               .build();
        } catch (IOException ioEx) {
            throw new IoException("Error sending HTTP request [" + request.getUri() + "]: " + ioEx.getMessage(), ioEx);
        } catch (InterruptedException intEx) {
            throw new IoException("Interrupted sending HTTP request [" + request.getUri() + "]: " + intEx.getMessage(), intEx);
        }
    }

    private java.net.http.HttpRequest.BodyPublisher toBodyHandler(HttpRequest request) {
        return new java.net.http.HttpRequest.BodyPublisher() {

            final Map<Flow.Subscriber<? super ByteBuffer>, Pair<Flow.Subscription, Long>> subscribers = new HashMap<>();

            @Override
            public long contentLength() {
                return request.getBody() == null ? -1 : request.getBody().getContentLength();
            }

            @Override
            public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
                Flow.Subscription subscription = new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        Pair<Flow.Subscription, Long> subscription = subscribers.get(subscriber);
                        if (subscription == null) {
                            return;
                        }
                        subscribers.computeIfPresent(subscriber, (k, p) -> Pair.of(p.getLeft(), p.getRight() + n));

                        if (request.getBody() != null) {
                            subscriber.onNext(ByteBuffer.wrap(request.getBody().readFullyAsBytes()));
                        } else {
                            subscription.getLeft().cancel();
                            subscriber.onComplete();
                        }
                    }

                    @Override
                    public void cancel() {
                        subscribers.remove(subscriber);
                    }
                };

                subscribers.put(subscriber, Pair.of(subscription, 0L));
                subscriber.onSubscribe(subscription);
            }
        };
    }

    private HttpClient.Version toJdkHttpClientVersion(final Request.Version version) {
        if (version == null) return HttpClient.Version.HTTP_1_1;

        return switch (version) {
            case HTTP_1_1 -> HttpClient.Version.HTTP_1_1;
            case HTTP_2 -> HttpClient.Version.HTTP_2;
            case HTTP_1_0 -> throw new UnsupportedOperationException("Http/1.0 versipon is unsupported by the JDK "
                    + HttpClient.class.getSimpleName() + " implementation.");
        };
    }
}
