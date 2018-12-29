package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.beanplanet.core.util.MultiValueMap;
import org.beanplanet.core.util.MultiValueMapImpl;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.beanplanet.core.util.EnumerationUtil.toList;
import static org.beanplanet.core.util.IterableUtil.nullSafeEnumerationIterable;


/**
 * A model of an HTTP request.
 *
 * @author Gary Watson
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "scheme",
        "method",
        "hostname",
        "path",
        "queryParams",
        "headers",
        "entity"
})
public class HttpRequest extends HttpMessage {
    /** The base parts (scheme://host/path) of the URI of the request. */
    private String baseUri;
    /** The HTTP method. */
    private String method;
    /** The scheme or protocol. */
    private String scheme;
    /** The host name in the request. */
    private String hostname;
    /** The port in the request. */
    private Integer port;
    /** The URI path. */
    private String path;

    /** The request URI path parameter names and values. */
     private Map<String, Object> pathParams;

    /** The request query parameters. */
    @JsonSerialize(using = MultiValueMapSerialiser.class)
    @JsonDeserialize(as = MultiValueMap.class, using = MultiValueMapDeserialiser.class)
    private MultiValueMap<String, String> queryParams;


    public HttpRequest() {
    }

    public HttpRequest(HttpServletRequest servletRequest) {
        MultiValueMap<String, String> headers = new MultiValueMapImpl<>();
        for (String headerName : nullSafeEnumerationIterable(servletRequest::getHeaderNames)) {
            Enumeration<String> headerValues = servletRequest.getHeaders(headerName);
            if ( headerValues != null) {
                headers.put(String.valueOf(headerName), toList(headerValues));
            }
        }
        setHeaders(headers);

        method = servletRequest.getMethod();
        hostname = servletRequest.getServerName();
        scheme = servletRequest.getScheme();
        try {
            path = new URIBuilder(servletRequest.getRequestURI().replace("//", "/")).getPath();
        } catch (URISyntaxException uriSyntaxEx) {
            throw new IllegalStateException(uriSyntaxEx);
        }

        MultiValueMap<String, String> queryParams = new MultiValueMapImpl<>();
        for (String queryParamName : nullSafeEnumerationIterable(servletRequest::getParameterNames)) {
            queryParams.put(queryParamName, Arrays.asList(servletRequest.getParameterValues(queryParamName)));
        }
        this.queryParams = queryParams;
    }

    public HttpRequest withHeader(String name, String value) { super.withHeader(name, value); return this; }

    public HttpRequest withHeaders(Map<String, Object> headers) { super.withHeaders(headers); return this; }

    public HttpRequest withPathParam(String name, Object value) {
        if (pathParams == null) {
            pathParams = new LinkedHashMap<>();
        }
        pathParams.put(name, value);
        return this;
    }

    public HttpRequest withPathParams(Map<String, Object> pathParams) {
        if (pathParams == null) {
            pathParams = new LinkedHashMap<>();
        }
        pathParams.putAll(pathParams);
        return this;
    }

    public HttpRequest withEntity(Resource entity) { super.withEntity(entity); return this; }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HttpRequest withMethod(String method) { setMethod(method); return this; }

    public String getBaseUri() {
        return baseUri;
    }

    public HttpRequest setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public HttpRequest withBaseUri(String baseUri) {
        setBaseUri(baseUri);
        return this;
    }

    public URI getRequestUri() {

        // Apply path parameters
        String transformedBaseUri = baseUri;
        if (transformedBaseUri != null) {
            if (pathParams != null && !pathParams.isEmpty()) {
                for (Map.Entry<String, Object> paramEntry : pathParams.entrySet()) {
                    transformedBaseUri = (transformedBaseUri == null ? null : transformedBaseUri.replaceAll("\\{" + paramEntry.getKey() + "\\}", URLEncoder.encode(String.valueOf(paramEntry.getValue()))));
                }
            }
        }

        try {
            URIBuilder uriBuilder = transformedBaseUri == null ? new URIBuilder() : new URIBuilder(transformedBaseUri);
            if (scheme != null) {
                uriBuilder.setScheme(scheme);
            }
            if (hostname != null) {
                uriBuilder.setHost(hostname);
            }
            if (port != null) {
                uriBuilder.setPort(port);
            }
            if (path != null) {
                // Join base URI and subsequent path elements
                uriBuilder.setPath((uriBuilder.getPath() == null ? "" : uriBuilder.getPath())+path);
            }
            if (queryParams != null && !queryParams.isEmpty()) {
                // Join base URI query params and specific query parameters
                for (String paramName : queryParams.keySet()) {
                    for (String value : queryParams.get(paramName)) {
                        uriBuilder.addParameter(paramName, value);
                    }
                }
            }

            return uriBuilder.build();
        } catch (URISyntaxException u) {
            throw new RuntimeException(u);
        }
    }

    public String getScheme() {
        if (scheme != null ) return scheme;

        try {
            if (baseUri == null) return null;

            return new URIBuilder(baseUri).getScheme();
        } catch (URISyntaxException uriEx) {
            throw new RuntimeException(uriEx);
        }
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public HttpRequest withScheme(String scheme) {
        setScheme(scheme);
        return this;
    }

    public String getHostname() {
        if (hostname != null ) return hostname;

        try {
            if (baseUri == null) return null;

            return new URIBuilder(baseUri).getHost();
        } catch (URISyntaxException uriEx) {
            throw new RuntimeException(uriEx);
        }
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public HttpRequest withHostname(String hostname) {
        setHostname(hostname);
        return this;
    }

    public Integer getPort() {
        if (port != null ) return port;

        try {
            if (baseUri == null) return null;

            return new URIBuilder(baseUri).getPort();
        } catch (URISyntaxException uriEx) {
            throw new RuntimeException(uriEx);
        }
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public HttpRequest withPort(Integer port) {
        setPort(port);
        return this;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpRequest withPath(String path) {
        setPath(path);
        return this;
    }

    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public HttpRequest withQueryParam(String name, String value) {
        if (queryParams == null) {
            this.queryParams = new MultiValueMapImpl<>();
        }

        queryParams.addValue(name, value);
        return this;
    }

    public String toString() {
        return new ReflectionToStringBuilder(this).build();
    }
}
