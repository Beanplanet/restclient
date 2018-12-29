package org.beanplanet.restclient.service;


import org.beanplanet.core.util.MultiValueMap;

import javax.servlet.http.Cookie;
import java.net.URI;
import java.util.List;

public interface RestBuilder {

    RestBuilder baseUri(String baseUri);
    RestBuilder path(String uriPath);
    RestBuilder header(String name, String value);
    RestBuilder contentType(String contentType);
    RestBuilder accept(String contentType);
    RestBuilder queryParam(String name, String value);
    RestBuilder entity(Object entity);
    RestBuilder cookies(List<Cookie> cookies);
    RestBuilder followRedirects(Boolean followRedirects);
    RestBuilder formParam(String name, String value);
    RestBuilder pathParam(String name, Object value);

    <T> T get(RestResponseHandler<T> handler);
    <T> T put(RestResponseHandler<T> handler);
    <T> T post(RestResponseHandler<T> handler);

    <T> T get(Class<T> responseType);
    <T> T get(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;
    <T> T put(Class<T> responseType);
    <T> T put(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;
    <T> T post(Class<T> responseType);
    <T> T post(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;

    RestResponse getResponse();
    RestResponse putResponse();
    RestResponse postResponse();

    String getBaseUri();
    URI getRequestUri();

    MultiValueMap<String, String> getHeaders();

    MultiValueMap<String, String> getQueryParams();

    Object getEntity();

    List<Cookie> getCookies();

    Boolean getFollowRedirects();

    MultiValueMap<String, String> getFormParams();
}
