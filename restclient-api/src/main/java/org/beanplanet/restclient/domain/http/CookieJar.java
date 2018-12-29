package org.beanplanet.restclient.domain.http;

import org.beanplanet.core.events.ChangeEvent;
import org.beanplanet.core.events.ChangeListener;
import org.beanplanet.core.events.EventSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookieJar implements CookieRegistry {
    private EventSupport eventSupport = new EventSupport();

    private List<CookieJarCookie> cookies = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean addCookie(CookieJarCookie cookie) {
        boolean added;
        synchronized (cookies) {
            added = cookies.add(new CookieJarCookieImpl(cookie));
        }

        if (added) {
            eventSupport.dispatchEvent(new ChangeEvent<>(this, null, cookie));
        }
        return added;
    }

    @Override
    public boolean deleteCookie(CookieJarCookie cookie) {
        boolean deleted;
        synchronized (cookies) {
            deleted = cookies.remove(cookie);
        }

        if (deleted) {
            eventSupport.dispatchEvent(new ChangeEvent<>(this, null, cookie));
        }
        return deleted;
    }

    @Override
    public boolean clearCookies() {
        boolean isEmpty;
        synchronized (cookies) {
            isEmpty = cookies.isEmpty();
            cookies.clear();
        }
        return !isEmpty;
    }

    @Override
    public boolean addChangeListener(ChangeListener<CookieJarCookie> listener) {
        return eventSupport.addListener(ChangeEvent.class, listener);
    }

    @Override
    public boolean removeChangeListener(ChangeListener<CookieJarCookie> listener) {
        return eventSupport.removeListener(ChangeEvent.class, listener);
    }
}
