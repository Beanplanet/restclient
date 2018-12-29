package org.beanplanet.restclient.domain.http;

import java.time.Instant;

public interface CookieJarCookie extends Cookie {
    boolean isHostOnly();
    Instant getLastAccessTime();
}
