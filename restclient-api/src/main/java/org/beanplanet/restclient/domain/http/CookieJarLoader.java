package org.beanplanet.restclient.domain.http;

public interface CookieJarLoader {
    CookieRegistry load();
    void save(CookieRegistry cookieRegistry);
}
