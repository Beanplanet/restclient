package org.beanplanet.restclient.service;

public interface RestResponseHandler<T> {
    T handleResponse(RestResponse response);
}
