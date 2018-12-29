package org.beanplanet.restclient.domain.http;

import org.beanplanet.core.events.ChangeEventSource;

/**
 * Defines a storage facility and Data Access Object (DAO) for cookies.
 */
public interface CookieRegistry extends ChangeEventSource<CookieJarCookie> {
    boolean addCookie(CookieJarCookie cookie);
    boolean deleteCookie(CookieJarCookie cookie);
    boolean clearCookies();
}
