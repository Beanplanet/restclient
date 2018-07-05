package org.beanplanet.restclient.httpclient.service;

import org.beanplanet.restclient.domain.http.HttpRequest;
import org.beanplanet.restclient.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An  REST service implmented using the Apache HTTP Components Client.
 *
 * @author Gary Watson
 */
public class HttpClientRestService implements RestService {
    private EntityProvider entityFactory = new EntityProviderRegistry()
            .addProvider(new JacksonJsonEntityProvider())
            .addProvider(new JacksonXmlEntityProvider())
            .addProvider(new CharsequenceEntityProvider());

    Map<Predicate<HttpRequest>, HttpClientConnectionManager> requestMapperConnectionManagers;
    HttpClientConnectionManager defaultConnectionManager;

    private Map<Predicate<org.beanplanet.restclient.domain.http.HttpRequest>, HttpConfiguration> configs = new LinkedHashMap<>();

    private HttpConfiguration defaultConfiguration = new HttpConfiguration();


    /**
     * Configure the service for requests matching the given predicate.
     *
     * @param requestMatcher a matcher of requests to configuration.
     * @param configuration the configuration which will be applied to requests matching the given request matcher.
     * @return the rest service for invocation chaining.
     */
    public HttpClientRestService config(Predicate<org.beanplanet.restclient.domain.http.HttpRequest> requestMatcher, HttpConfiguration configuration) {
        configs.put(requestMatcher, configuration);
        return this;
    }

    /**
     * Gets the default configuration which will be applied to requests not accepted by any other request matcher.
     *
     * @return the default configuration which will be applied to requests not accepted by any other request matcher.
     */
    public HttpConfiguration config() {
        return defaultConfiguration;
    }

    /**
     * Configure the default configuration which will be applied to requests not accepted by any other request matcher. This is equivalent to a call
     * <code>configure(true, Configuration)</code>, matching all requests after all other matchers have first been given the opportunity to match first.
     *
     * @param configuration the default configuration which will be applied to requests not accepted by any other request matcher.
     * @return the rest service for invocation chaining.
     */
    public HttpClientRestService config(HttpConfiguration configuration) {
        this.defaultConfiguration = configuration;
        return this;
//        return config(PredicateUtils.<com.bbc.store.restclient.domain.http.HttpRequest>truePredicate(), configuration);
    }

    @PostConstruct
    public void initialise() throws Exception {
        requestMapperConnectionManagers = new LinkedHashMap<>();
        if ( !configs.isEmpty() ) {
            for (Map.Entry<Predicate<HttpRequest>, HttpConfiguration> mappedConfigEntry : configs.entrySet()) {
                requestMapperConnectionManagers.put(mappedConfigEntry.getKey(), createConnectionManager(mappedConfigEntry.getValue()));
            }
        }

        defaultConnectionManager = createConnectionManager(defaultConfiguration);
    }

    private HttpClientConnectionManager createConnectionManager(HttpConfiguration configuration) throws Exception {
        //==============================================================================================================
        // TLS/SSL Configuration
        //==============================================================================================================
        KeyStore trustStore = null, clientKeyStore = null;

        //--------------------------------------------------------------------------------------------------------------
        // Trust configuration
        //--------------------------------------------------------------------------------------------------------------
        if (configuration.tlsConfig() != null && configuration.tlsConfig().getTrustStore() != null) {
            trustStore = loadKeyStore(configuration.tlsConfig().getTrustStore(),
                                      configuration.tlsConfig().getTrustStoreType(),
                                      configuration.tlsConfig().getTrustStorePassword());
        }

        //--------------------------------------------------------------------------------------------------------------
        // Client key configuration
        //--------------------------------------------------------------------------------------------------------------
        if (configuration.tlsConfig() != null && configuration.tlsConfig().getClientKeyStore() != null) {
            clientKeyStore = loadKeyStore(configuration.tlsConfig().getClientKeyStore(),
                                          configuration.tlsConfig().getClientKeyStoreType(),
                                          configuration.tlsConfig().getClientKeyStorePassword());
        }

        //==============================================================================================================
        // Connection Configuration
        //==============================================================================================================


        RegistryBuilder<ConnectionSocketFactory> connectionSocketFactoryRegistryBuilder = RegistryBuilder.create();
        connectionSocketFactoryRegistryBuilder.register("http", new PlainConnectionSocketFactory());

        if (trustStore != null || clientKeyStore != null) {
            SSLContextBuilder sslContextBuilder = SSLContexts.custom();

            if (trustStore != null) {
                sslContextBuilder.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy());
            }

            if (clientKeyStore != null) {
                sslContextBuilder.loadKeyMaterial(clientKeyStore, configuration.tlsConfig().getClientKeyStorePassword().toCharArray());
            }

            SSLContext sslContext = sslContextBuilder.build();
            connectionSocketFactoryRegistryBuilder.register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));

        } else {
            // Configure using the system connection socket factory
            connectionSocketFactoryRegistryBuilder.register("https", SSLConnectionSocketFactory.getSystemSocketFactory());
        }

        Registry<ConnectionSocketFactory> connectionSocketFactoryRegistry = connectionSocketFactoryRegistryBuilder.build();

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(connectionSocketFactoryRegistry);
        poolingHttpClientConnectionManager.setMaxTotal(400);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(300);
        HttpClientConnectionManager httpClientConnectionManager = poolingHttpClientConnectionManager;

        return httpClientConnectionManager;
    }

    @PreDestroy
    public void shutdown() {

    }

    public static KeyStore loadKeyStore(Resource keyStoreResource, String keyStoreType, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance(keyStoreType);

        try (InputStream is = keyStoreResource.getInputStream()) {
            trustStore.load(is, password.toCharArray());
        }

        return trustStore;
    }

    /**
     * Configure the service for requests matching the given predicate.
     *
     * @param requestMatcher a matcher of requests to configuration.
     * @param configuration  the configuration which will be applied to requests matching the given request matcher.
     * @return the rest service for invocation chaining.
     */
    @Override
    public RestService config(Predicate<org.beanplanet.restclient.domain.http.HttpRequest> requestMatcher, Configuration configuration) {
        return null;
    }

    /**
     * Configure the default configuration which will be applied to requests not accepted by any other request matcher. This is equivalent to a call
     * <code>configure(true, Configuration)</code>, matching all requests after all other matchers have first been given the opportunity to match first.
     *
     * @param configuration the default configuration which will be applied to requests not accepted by any other request matcher.
     * @return the rest service for invocation chaining.
     */
    @Override
    public RestService config(Configuration configuration) {
        return null;
    }

    @Override
    public HttpCommonsClientRestBuilder createRestBuilder() {
        return new HttpCommonsClientRestBuilder();
    }

    public class HttpCommonsClientRestBuilder implements RestBuilder {
//        private URIBuilder uriBuilder = new URIBuilder();

        private HttpRequest request = new HttpRequest();

        private Object                         entity;
        private List<Cookie>                   cookies;
        private Boolean                        followRedirects;
        private MultiValuedMap<String, String> formParams;

        @Override
        public HttpCommonsClientRestBuilder baseUri(String baseUri) {
            request.setBaseUri(baseUri);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder path(String uriPath) {
            request.setPath(uriPath);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder header(String name, String value) {
            request.withHeader(name, value);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder contentType(String contentType) {
            header("Content-Type", contentType);
            return this;
        }

        public HttpCommonsClientRestBuilder contentType(ContentType contentType) {
            contentType(contentType.getMimeType());
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder accept(String contentType) {
            header("Accept", contentType);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder queryParam(String name, String value) {
            request.withQueryParam(name, value);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder cookies(List<Cookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder followRedirects(Boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder formParam(String name, String value) {
            if (formParams == null) {
                this.formParams = new ArrayListValuedHashMap<>();
            }
            formParams.put(name, value);
            return this;
        }

        @Override
        public HttpCommonsClientRestBuilder pathParam(String name, Object value) {
            request.withPathParam(name, value);
            return this;
        }

        @Override
        public <T> T get(RestResponseHandler<T> handler) {
            return execute(new HttpGet(getRequestUri()), handler, true);
        }

        @Override
        public <T> T put(RestResponseHandler<T> handler) {
            return execute(new HttpPut(getRequestUri()), handler, true);
        }

        private ContentType determineContentType() {
            ContentType contentType = null;
            if (request.getHeaders() != null) {
                Collection<String> contentTypeHeaders = CollectionUtils.select(request.getHeaders().keySet(),
                                                                   new Predicate<String>() {
                                                                       @Override
                                                                       public boolean evaluate(String h) {
                                                                           return "content-type".equalsIgnoreCase(h);
                                                                       }
                                                                   });

                if ( !contentTypeHeaders.isEmpty() ) {
                    Collection<String> contentTypeHeaderValues = request.getHeaders().get(contentTypeHeaders.iterator().next());

                    if (!contentTypeHeaderValues.isEmpty()) {
                        contentType = ContentType.create(contentTypeHeaderValues.iterator().next());
                    }
                }
            }

            return contentType != null ? contentType : ContentType.APPLICATION_OCTET_STREAM;
        }

        @Override
        public <T> T post(RestResponseHandler<T> handler) {
            return execute(new HttpPost(getRequestUri()), handler, true);
        }

        public <T> T get(Class<T> responseType) {
            return execute(new HttpGet(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(responseType), true);
        }

        public <T> T get(int expectedHttpStatusCode, Class<T> responseType) {
            return execute(new HttpGet(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(expectedHttpStatusCode, responseType), true);
        }

        public <T> T put(Class<T> responseType) {
            return execute(new HttpPut(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(responseType), true);
        }

        public <T> T put(int expectedHttpStatusCode, Class<T> responseType) {
            return execute(new HttpPut(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(expectedHttpStatusCode, responseType), true);
        }

        public <T> T post(Class<T> responseType) {
            return execute(new HttpPost(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(responseType), true);
        }

        public <T> T post(int expectedHttpStatusCode, Class<T> responseType) {
            return execute(new HttpPost(getRequestUri()), new TypeConvertingRestResponseEntityHandler<>(expectedHttpStatusCode, responseType), true);
        }

        private <T> T execute(HttpRequestBase httpMethod, RestResponseHandler<T> handler, boolean closeResponse) {
            request.setMethod(httpMethod.getMethod());
            HttpClientConnectionManager httpClientConnectionManager = determineConnectionManagerForRequest(request);
            CloseableHttpClient httpClient = HttpClients
                    .custom()
                    .setConnectionManager(httpClientConnectionManager).build();

            // Apply redirect handling
            if (followRedirects != null) {
                httpMethod.setConfig(RequestConfig.custom().setRedirectsEnabled(followRedirects).build());
            }

            // Apply headers
            if (request.getHeaders() != null) {
                for (String header : request.getHeaders().keySet()) {
                    for (String value : request.getHeaders().get(header)) {
                        httpMethod.addHeader(header, value);
                    }
                }
            }

            // Apply entity
            if (entity != null && (httpMethod instanceof HttpEntityEnclosingRequestBase)) {
                ContentType contentType = determineContentType();
                HttpEntity httpEntity = entityFactory.createEntityForObject(httpMethod, entity);
                ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(httpEntity);
            }

            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpMethod);
                return handler.handleResponse(new HttpClientResponse(response));
            } catch (IOException ioEx) {
                throw new RuntimeException(ioEx);
            } finally {
                if (closeResponse && response != null) {
                    try {
                        response.close();
                    } catch (IOException closeEx) {
                        throw new RuntimeException(closeEx);
                    }
                }
            }
        }

        private HttpClientConnectionManager determineConnectionManagerForRequest(HttpRequest httpRequest) {
            if ( requestMapperConnectionManagers != null && !requestMapperConnectionManagers.isEmpty() ) {
                for (Map.Entry<Predicate<HttpRequest>, HttpClientConnectionManager> requestMapping : requestMapperConnectionManagers.entrySet()) {
                    if (requestMapping.getKey().evaluate(httpRequest)) {
                        return requestMapping.getValue();
                    }
                }
            }

            return defaultConnectionManager;
        }

        @Override
        public RestResponse getResponse() {
            return execute(new HttpGet(getRequestUri()), new RestResponseReturningHandler(), false);
        }

        @Override
        public RestResponse putResponse() {
            return execute(new HttpPut(getRequestUri()), new RestResponseReturningHandler(), false);
        }

        @Override
        public RestResponse postResponse() {
            return execute(new HttpPost(getRequestUri()), new RestResponseReturningHandler(), false);
        }

        @Override
        public String getBaseUri() {
            return request.getBaseUri();
        }

        @Override
        public URI getRequestUri() {
            return request.getRequestUri();
        }

        @Override
        public MultiValuedMap<String, String> getHeaders() {
            return request.getHeaders();
        }

        @Override
        public MultiValuedMap<String, String> getQueryParams() {
            return request.getQueryParams();
        }

        @Override
        public Object getEntity() {
            return entity;
        }

        @Override
        public List<Cookie> getCookies() {
            return cookies;
        }

        @Override
        public Boolean getFollowRedirects() {
            return followRedirects;
        }

        @Override
        public MultiValuedMap<String, String> getFormParams() {
            return formParams;
        }
    }

    class HttpClientResponse implements RestResponse {
        private CloseableHttpResponse response;

        public HttpClientResponse(CloseableHttpResponse response) {
            this.response = response;
        }

        @Override
        public int getStatusCode() {
            return response.getStatusLine().getStatusCode();
        }

        @Override
        public List<Cookie> getCookies() {
            throw new UnsupportedOperationException("Not implemented, yet!");
        }

        @Override
        public String getHeader(String name) {
            Header lastHeader = response.getLastHeader(name);
            return lastHeader != null ? lastHeader.getValue() : null;
        }

        @Override
        public Navigator navigator() {
            return new GroovyNavigator(getEntity(Object.class));
        }

        @Override
        public <T> T getEntity(Class<T> clazz) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new RuntimeException("The response did not contain a message body.");
            }

            T objectForEntity = null;
            if ( entityFactory.canCreateObjectForEntity(response, entity, clazz)) {
                objectForEntity = entityFactory.createObjectForEntity(response, entity, clazz);
            }

            if ( objectForEntity == null) {
                throw new RuntimeException(String.format("Unable to create an object for the given response content type [%s]", response.getLastHeader(HttpHeaders.CONTENT_TYPE)));
            }

            return objectForEntity;
        }

        @Override
        public String getEntityAsString() {
            throw new UnsupportedOperationException("Not implemented, yet!");
        }

        @Override
        public void close() throws IOException {
            response.close();
        }

        @Override
        public void closeQuietly() {
            try { close(); } catch (IOException ignoreEx){}
        }
    }
}
