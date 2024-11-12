package org.beanplanet.restclient;

import org.beanplanet.core.io.resource.CharSequenceResource;
import org.beanplanet.core.io.resource.Resource;
import org.beanplanet.core.lang.Assert;
import org.beanplanet.core.lang.TypeUtil;
import org.beanplanet.core.net.http.*;
import org.beanplanet.core.net.http.converter.HttpMessageBodyConverter;
import org.beanplanet.core.net.http.converter.HttpMessageBodyConverterRegistry;
import org.beanplanet.core.net.http.converter.SystemHttpMessageBodyConverterRegistry;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

/**
 * REST client main service and entry point.
 */
public class RestClient implements HttpRequest.HttpRequestBuilderSpec<RestClient.RequestBuilder> {
    private final boolean applyDefaultClientServerErrorResponseHandlers;
    private final HttpImplemention httpImplemention;
    private final HttpMessageBodyConverterRegistry handlerRegistry;
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
        private HttpMessageBodyConverterRegistry handlerRegistry = SystemHttpMessageBodyConverterRegistry.getInstance();
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

        public RestClientBuilder messageBodyHandlers(final HttpMessageBodyConverterRegistry handlerRegistry) {
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

    public class RequestBuilder implements HttpRequest.HttpRequestBuilderSpec<RequestBuilder> {
        private final HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder;
        private Object body;

        private Request request;

        private RequestBuilder(final HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder, Object body) {
            this.httpRequestBuilder = httpRequestBuilder;
            this.body = body;
        }

        private RequestBuilder() {
            this(HttpRequest.builder(), null);
        }

        public RequestBuilder method(String methodOrExtension) {
            httpRequestBuilder.method(methodOrExtension);
            return this;
        }

        public RequestBuilder uri(final URI uri) {
            httpRequestBuilder.uri(uri);
            return this;
        }

        public RequestBuilder headers(final Consumer<HttpHeaders.HttpHeadersBuilder> headersBuilderConsumer) {
            httpRequestBuilder.headers(headersBuilderConsumer);
            return this;
        }

        public RequestBuilder headers(final HttpHeaders headers) {
            httpRequestBuilder.headers(headers);
            return this;
        }

        public RequestBuilder headers(final Map<String, List<String>> headers) {
            httpRequestBuilder.headers(headers);
            return this;
        }

        public RequestBuilder header(final String name, final String value) {
            httpRequestBuilder.header(name, value);
            return this;
        }

        public RequestBuilder cookie(final Cookie cookie) {
            httpRequestBuilder.cookie(cookie);
            return this;
        }

        public RequestBuilder contentType(final MediaType mediaType) {
            httpRequestBuilder.contentType(mediaType);
            return this;
        }

        public RequestBuilder contentType(final String mediaType) {
            httpRequestBuilder.contentType(mediaType);
            return this;
        }

        @Override
        public RequestBuilder contentType(MediaType mediaType, Charset charset) {
            httpRequestBuilder.contentType(mediaType, charset);
            return this;
        }

        public RequestBuilder contentLength(final long length) {
            httpRequestBuilder.contentLength(length);
            return this;
        }

        public RequestBuilder version(final Request.Version version) {
            httpRequestBuilder.version(version);
            return this;
        }

        public RequestBuilder request(Consumer<HttpRequest.HttpRequestBuilder<?, ?>> requestBuilderConsumer) {
            requestBuilderConsumer.accept(httpRequestBuilder);
            return this;
        }

        public RequestBuilder body(final Resource body, final MediaType mediaType, final Charset charset) {
            httpRequestBuilder.body(body, mediaType, charset);
            return this;
        }

        public RequestBuilder body(final Resource body, final MediaType mediaType) {
            return body(body, mediaType, Charset.defaultCharset());
        }

        public RequestBuilder body(final Resource body) {
            return body(body, null, null);
        }

        public RequestBuilder body(Object body) {
            this.body = body;
            return this;
        }

        @SuppressWarnings("unchecked")
        private <T> Resource bodyFromConversion(final T body) {
            Assert.notNull(request, "Something has occurred out of order - the HTTP request has not yet been built!");
            MediaType mediaType = request.getContentType().orElse(MediaTypes.Application.OCTET_STREAM);
            HttpMessageBodyConverter<T> handler = handlerRegistry.findFromConverters(mediaType, (Class<T>) body.getClass())
                                                                 .findFirst().orElseThrow(() -> new RestClientException("No handler configured to send request body [" + body.getClass() + "] of media type [" + mediaType.getName() + "]."));
            return handler.convertTo(body, request);
        }

        public ExecutedRequest execute() {
            HttpRequest request = requestPrototype != null ? requestPrototype.merge(httpRequestBuilder.build()) : httpRequestBuilder.build();
            this.request = request;

            if (body != null) {
                request = request.merge(HttpRequest.builder().method(request.getMethod()).body(bodyFromConversion(body)).build());
            }

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
                    return handlerRegistry.findFromConverters(mediaType, responseType)
                                          .findFirst()
                                          .map(h -> h.convertFrom(responseType, response))
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
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.request(requestBuilderConsumer);
        return requestBuilder;
    }

    public RequestBuilder method(String methodOrExtension) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.method(methodOrExtension);
        return requestBuilder;
    }

    public RequestBuilder uri(final URI uri) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.uri(uri);
        return requestBuilder;
    }

    public RequestBuilder version(final Request.Version version) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.version(version);
        return requestBuilder;
    }

    public RequestBuilder headers(final Consumer<HttpHeaders.HttpHeadersBuilder> headersBuilderConsumer) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.headers(headersBuilderConsumer);
        return requestBuilder;
    }

    public RequestBuilder headers(final HttpHeaders headers) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.headers(headers);
        return requestBuilder;
    }

    public RequestBuilder headers(final Map<String, List<String>> headers) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.headers(headers);
        return requestBuilder;
    }

    public RequestBuilder header(final String name, final String value) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.header(name, value);
        return requestBuilder;
    }

    public RequestBuilder cookie(final Cookie cookie) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.header(Cookie.HTTP_REQUEST_HEADER_NAME, cookie.toHttpRequestHeaderValue());
        return requestBuilder;
    }

    public RequestBuilder contentType(final MediaType mediaType) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.contentType(mediaType);
        return requestBuilder;
    }

    public RequestBuilder contentType(final String mediaType) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.contentType(mediaType);
        return requestBuilder;
    }

    @Override
    public RequestBuilder contentType(MediaType mediaType, Charset charset) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.contentType(mediaType, charset);
        return requestBuilder;
    }

    public RequestBuilder contentLength(final long length) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, null);
        requestBuilder.contentLength(length);
        return requestBuilder;
    }

    public RequestBuilder body(final Resource body, final MediaType mediaType, final Charset charset) {
        HttpRequest.HttpRequestBuilder<?, ?> httpRequestBuilder = HttpRequest.builder();
        RequestBuilder requestBuilder = new RequestBuilder(httpRequestBuilder, body);

        if (mediaType != null) {
            httpRequestBuilder.contentType(mediaType, charset != null ? charset : mediaType.getParameters().getCharset().orElse(Charset.defaultCharset()));
        }
        return requestBuilder;
    }

    public RequestBuilder body(final Resource body, final MediaType mediaType) {
        return body(body, mediaType, Charset.defaultCharset());
    }

    public RequestBuilder body(final CharSequence string) {
        return body(string == null ? null : new CharSequenceResource(string), null, null);
    }

    public RequestBuilder body(final Resource body) {
        return body(body, null, null);
    }

    public RequestBuilder body(Object body) {
        RequestBuilder requestBuilder = new RequestBuilder(HttpRequest.builder(), null);
        requestBuilder.body(body);
        return requestBuilder;
    }
}
