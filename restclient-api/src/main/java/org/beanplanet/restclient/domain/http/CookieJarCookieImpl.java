package org.beanplanet.restclient.domain.http;

import java.time.Instant;

public class CookieJarCookieImpl extends HttpCookie implements CookieJarCookie {
    private boolean hostOnly;
    private Instant lastAccessTime;

    public CookieJarCookieImpl() {}

    public CookieJarCookieImpl(CookieJarCookie other) {
        super(other);
        this.hostOnly = other.getHttpOnly();
        this.lastAccessTime = other.getLastAccessTime();
    }

    @Override
    public boolean isHostOnly() {
        return hostOnly;
    }

    public void setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
    }

    public CookieJarCookieImpl withHostOnly(boolean hostOnly) {
        setHostOnly(hostOnly);
        return this;
    }

    @Override
    public Instant getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Instant lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public CookieJarCookie withLastAccessTime(Instant lastAccessTime) {
        setLastAccessTime(lastAccessTime);
        return this;
    }
}
