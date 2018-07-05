package org.beanplanet.restclient.service;

import javax.servlet.http.Cookie;
import java.io.Closeable;
import java.util.List;

public interface RestResponse extends Closeable {
    int getStatusCode();
    List<Cookie> getCookies();
    String getHeader(String name);
    Navigator navigator();

    <T> T getEntity(Class<T> clazz);
    String getEntityAsString();

    void closeQuietly();
}
