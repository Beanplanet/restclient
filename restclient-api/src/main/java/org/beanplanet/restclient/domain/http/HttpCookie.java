package org.beanplanet.restclient.domain.http;

import java.util.Objects;

public class HttpCookie implements Cookie {
    private String name;
    private String value;
    private String comment;
    private String domain;
    private Integer maxAge;
    private String path;
    private Boolean secure;
    private Integer version;
    private Boolean httpOnly;

    public HttpCookie() {}

    public HttpCookie(Cookie other) {
        this.name = other.getName();
        this.value = other.getValue();
        this.comment = other.getComment();
        this.domain = other.getDomain();
        this.maxAge = other.getMaxAge();
        this.path = other.getPath();
        this.secure = other.isSecure();
        this.version = other.getVersion();
        this.httpOnly = other.getHttpOnly();
    }

    @SuppressWarnings("unchecked")
    <B extends Cookie> B withBuilder(Class<B> builder) {
        return (B)this;
    }

    @Override
    public String getName() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HttpCookie withName(String name) {
        setName(name);
        return this;
    }

    @Override
    public String getValue() {
        return null;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HttpCookie withValue(String value) {
       setValue(value);
       return this;
    }

    @Override
    public String getComment() {
        return null;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HttpCookie withComment(String comment) {
        setComment(comment);
        return this;
    }

    @Override
    public String getDomain() {
        return null;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public HttpCookie withDomain(String domain) {
        setDomain(domain);
        return this;
    }

    @Override
    public Integer getMaxAge() {
        return null;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public HttpCookie withMaxAge(Integer maxAge) {
        setMaxAge(maxAge);
        return this;
    }

    @Override
    public String getPath() {
        return null;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpCookie withPath(String path) {
        setPath(path);
        return this;
    }

    @Override
    public Boolean isSecure() {
        return null;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public HttpCookie withSecure(Boolean secure) {
        setSecure(secure);
        return this;
    }

    @Override
    public Integer getVersion() {
        return null;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public HttpCookie withVersion(Integer version) {
        setVersion(version);
        return this;
    }

    @Override
    public Boolean getHttpOnly() {
        return null;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public HttpCookie withHttpOnly(Boolean httpOnly) {
        setHttpOnly(httpOnly);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(name, that.name)
               && Objects.equals(value, that.value)
               && Objects.equals(comment, that.comment)
               && Objects.equals(domain, that.domain)
               && Objects.equals(maxAge, that.maxAge)
               && Objects.equals(path, that.path)
               && Objects.equals(secure, that.secure)
               && Objects.equals(version, that.version)
               && Objects.equals(httpOnly, that.httpOnly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, comment, domain, maxAge, path, secure, version, httpOnly);
    }
}
