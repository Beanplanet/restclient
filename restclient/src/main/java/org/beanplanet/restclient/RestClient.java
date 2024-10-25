package org.beanplanet.restclient;

import org.beanplanet.core.lang.TypeUtil;
import org.beanplanet.core.net.http.*;
import org.beanplanet.core.net.http.handler.HttpMessageBodyHandlerRegistry;
import org.beanplanet.core.net.http.handler.SystemHttpMessageBodyHandlerRegistry;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

/**
 * REST client main service and entry point.
 */
public class RestClient {
    private final boolean applyDefaultClientServerErrorResponseHandlers;
    private final HttpImplemention httpImplemention;
    private final HttpMessageBodyHandlerRegistry handlerRegistry;
    private HttpRequest requestPrototype;

    private RestClient(final RestClientBuilder builder) {
        this.applyDefaultClientServerErrorResponseHandlers = builder.applyDefaultClientServerErrorResponseHandlers;
        this.httpImplemention = builder.httpImplemention;
        this.handlerRegistry = builder.handlerRegistry;
        this.requestPrototype = builder.requestPrototype;
    }

    private RestClient() {
        this(new RestClientBuilder());
    }


    /**
     * Creates a builder for creating {@link RestClient} configured instances.
     *
     * @return a new builder for creating RESTful clients.
     */
    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    public static class RestClientBuilder {
        private boolean applyDefaultClientServerErrorResponseHandlers = true;
        private HttpImplemention httpImplemention = new JdkHttpClientImplementation();
        private HttpMessageBodyHandlerRegistry handlerRegistry = SystemHttpMessageBodyHandlerRegistry.getInstance();
        private HttpRequest requestPrototype;

        public RestClientBuilder applyDefaultClientServerErrorResponseHandlers(final boolean applyDefaultClientServerErrorResponseHandlers) {
            this.applyDefaultClientServerErrorResponseHandlers = applyDefaultClientServerErrorResponseHandlers;
            return this;
        }

        public RestClientBuilder enableDefaultClientServerErrorResponseHandlers() {
            return applyDefaultClientServerErrorResponseHandlers(true);
        }

        public RestClientBuilder disableDefaultClientServerErrorResponseHandlers() {
            return applyDefaultClientServerErrorResponseHandlers(false);
        }

        public RestClientBuilder httpImplementation(final HttpImplemention httpImplemention) {
            this.httpImplemention = httpImplemention;
            return this;
        }

        public RestClientBuilder messageBodyHandlers(final HttpMessageBodyHandlerRegistry handlerRegistry) {
            this.handlerRegistry = handlerRegistry;
            return this;
        }

        public RestClientBuilder requestPrototype(final HttpRequest requestPrototype) {
            this.requestPrototype = requestPrototype;
            return this;
        }

        public RestClientBuilder requestPrototype(final Consumer<HttpRequest.HttpRequestBuilder<?, ?>> requestPrototypeBuilder) {
            HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
            requestPrototypeBuilder.accept(httpRequestBuilder);
            return requestPrototype(httpRequestBuilder.build());
        }

        public RestClient build() {
            return new RestClient(this);
        }
    }

    public class RequestBuilder {
        private final HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder;

        private RequestBuilder(final HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder) {
            this.httpRequestBuilder = httpRequestBuilder;
        }

        private RequestBuilder() {
            this(HttpRequest.builder());
        }

        public ExecutedRequest execute() {
            HttpRequest request = requestPrototype != null ? requestPrototype.merge(httpRequestBuilder.build()) : httpRequestBuilder.build();

            HttpResponse response = httpImplemention.execute(request);
            return new ExecutedRequest(request, response);
        }

        public class ExecutedRequest {
            private final HttpRequest request;
            private final HttpResponse response;

            public ExecutedRequest(final HttpRequest request, final HttpResponse response) {
                this.request = request;
                this.response = response;
            }

            public ExecutedRequest peek(BiConsumer<Request, Response> peek) {
                peek.accept(request, response);
                return this;
            }

            public ExecutedRequest peek(Consumer<Response> peek) {
                peek.accept(response);
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusThrow(Predicate<HttpResponseStatus> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (condition.test(response.getStatus())) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusThrow(Predicate<HttpResponseStatus> condition, Function<Response, E> exceptionFactory) {
                if (condition.test(response.getStatus())) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusThrow(Predicate<HttpResponseStatus> condition, Supplier<E> exceptionFactory) {
                if (condition.test(response.getStatus())) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(int statusCode, BiFunction<Request, Response, E> exceptionFactory) {
                if (statusCode == response.getStatusCode()) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(int statusCode, Function<Response, E> exceptionFactory) {
                if (statusCode == response.getStatusCode()) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(int statusCode, Supplier<E> exceptionFactory) {
                if (statusCode == response.getStatusCode()) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(Predicate<Integer> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (condition.test(response.getStatusCode())) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(Predicate<Integer> condition, Function<Response, E> exceptionFactory) {
                if (condition.test(response.getStatusCode())) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeThrow(Predicate<Integer> condition, Supplier<E> exceptionFactory) {
                if (condition.test(response.getStatusCode())) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseThrow(Predicate<Response> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (condition.test(response)) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseThrow(Predicate<Response> condition, Function<Response, E> exceptionFactory) {
                if (condition.test(response)) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseThrow(Predicate<Response> condition, Supplier<E> exceptionFactory) {
                if (condition.test(response)) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusNotThenThrow(Predicate<HttpResponseStatus> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (!condition.test(response.getStatus())) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusNotThenThrow(Predicate<HttpResponseStatus> condition, Function<Response, E> exceptionFactory) {
                if (!condition.test(response.getStatus())) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusNotThenThrow(Predicate<HttpResponseStatus> condition, Supplier<E> exceptionFactory) {
                if (!condition.test(response.getStatus())) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(int statusCode, BiFunction<Request, Response, E> exceptionFactory) {
                if (statusCode != response.getStatusCode()) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(int statusCode, Function<Response, E> exceptionFactory) {
                if (statusCode != response.getStatusCode()) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(int statusCode, Supplier<E> exceptionFactory) {
                if (statusCode != response.getStatusCode()) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(Predicate<Integer> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (!condition.test(response.getStatusCode())) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(Predicate<Integer> condition, Function<Response, E> exceptionFactory) {
                if (!condition.test(response.getStatusCode())) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onStatusCodeNotThenThrow(Predicate<Integer> condition, Supplier<E> exceptionFactory) {
                if (!condition.test(response.getStatusCode())) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseNotThenThrow(Predicate<Response> condition, BiFunction<Request, Response, E> exceptionFactory) {
                if (!condition.test(response)) {
                    throw exceptionFactory.apply(request, response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseNotThenThrow(Predicate<Response> condition, Function<Response, E> exceptionFactory) {
                if (!condition.test(response)) {
                    throw exceptionFactory.apply(response);
                }
                return this;
            }

            public <E extends RuntimeException> ExecutedRequest onResponseNotThenThrow(Predicate<Response> condition, Supplier<E> exceptionFactory) {
                if (!condition.test(response)) {
                    throw exceptionFactory.get();
                }
                return this;
            }

            public <T> T body(final Predicate<HttpResponseStatus> condition,
                                    final BiFunction<Request, Response, T> converter) throws RestErrorResponseException {
                if (!condition.test(response.getStatus())) {
                    throw new RestErrorResponseException(request.getUri().toString(), response.getStatusCode());
                }

                return converter.apply(request, response);
            }

            public <T> T body(final Predicate<HttpResponseStatus> condition,
                                    final Function<Response, T> converter) throws RestErrorResponseException {
                if (!condition.test(response.getStatus())) {
                    throw new RestErrorResponseException(request.getUri().toString(), response.getStatusCode());
                }

                return converter.apply(response);
            }

            public <T> T body(final Predicate<HttpResponseStatus> condition,
                                    final Class<T> responseType) throws RestErrorResponseException {
                if (!condition.test(response.getStatus())) {
                    throw new RestErrorResponseException(request.getUri().toString(), response.getStatusCode());
                }

                return bodyConverterFor(responseType).apply(response);
            }

            public <T> T body(final Class<T> responseType) throws RestErrorResponseException {
                return applyDefaultClientServerErrorResponseHandlers
                        ? bodyOn2xxSuccessful(responseType)
                        : body(response -> bodyConverterFor(responseType).apply(response));
            }

            public <T> CompletableFuture<T> bodyCompletable(final Class<T> responseType) throws RestErrorResponseException {
                return CompletableFuture.completedFuture(
                        applyDefaultClientServerErrorResponseHandlers
                        ? bodyOn2xxSuccessful(responseType)
                        : body(response -> bodyConverterFor(responseType).apply(response))
                );
            }

            public <T> T bodyOn2xxSuccessful(final Class<T> responseType) throws RestErrorResponseException {
                return body(ResponseStatus::is2xxSuccessful, responseType);
            }

            public <T> T body(final BiFunction<Request, Response, T> converter) throws RestErrorResponseException {
                return converter.apply(request, response);
            }

            public <T> T body(final Function<Response, T> converter) throws RestErrorResponseException {
                return converter.apply(response);
            }

            public CompletableFuture<Response> responseAsCompletable() throws RestErrorResponseException {
                return CompletableFuture.completedFuture(response);
            }

            public HttpResponse response() {
                return response;
            }

            private <T> Function<Response, T> bodyConverterFor(final Class<T> responseType) {
                return response -> {
                    final MediaType mediaType = response.getContentType().orElse(MediaTypes.Application.OCTET_STREAM);
                    return handlerRegistry.findReadHandlers(mediaType, responseType)
                                          .findFirst()
                                          .map(h -> h.read(responseType, response))
                                          .orElseThrow(() -> new RestClientException("Unable to read type [" + TypeUtil.getBaseName(responseType)
                                                  + "] from HTTP response media type [" + mediaType.getName()
                                                  + "]: no HTTP message converter between those types was found"
                                          ));
                };
            }
        }
    }


    /**
     * Creates a builder of HTTP Requests, ready to then be executed,
     *
     * @return a new builder for creating RESTful clients.
     */
    public RequestBuilder requestBuilder() {
        return new RequestBuilder();
    }

    /**
     * Creates a builder of HTTP Requests, ready to then be executed,
     *
     * @return a new builder for creating RESTful clients.
     */
    public RequestBuilder request(Consumer<HttpRequest.HttpRequestBuilder<?, ?>> requestBuilderConsumer) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        requestBuilderConsumer.accept(httpRequestBuilder);

        return new RequestBuilder(httpRequestBuilder);
    }

    public static void main(String... args) throws Exception {
        RestClient restClient = RestClient.builder().build();
        System.out.println(
                restClient
                        .request(r -> r.get("https://www.google.co.uk/")
                                       .header("Accept", "*/*")
                        )
                        .execute()
                        .response().getBody().readFullyAsString()
        );

        System.out.println(
                restClient
                        .request(r -> r.get("https://www.google.com")
                        )
                        .execute()
                        .body(String.class)
        );

        System.out.println(
                HttpClient.newBuilder()
                          .build()
                          .send(java.net.http.HttpRequest.newBuilder()
                                                         .uri(URI.create("https://www.google.com"))
                                                         .method("GET", java.net.http.HttpRequest.BodyPublishers.noBody())
                                                         .build(),
                                  java.net.http.HttpResponse.BodyHandlers.ofString()
                          ).body()
        );

        System.out.println(
                restClient
                        .request(r -> r.get("https://www.google.co.uk/")
                                       .header("Accept", "*/*")
                        )
                        .execute()
                        .body(byte[].class)
        );
    }
}
