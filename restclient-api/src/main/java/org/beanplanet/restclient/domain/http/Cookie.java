package org.beanplanet.restclient.domain.http;

public interface Cookie {
    String getName();
    String getValue();
    String getComment();
    String getDomain();
    Integer getMaxAge();
    String getPath();
    Boolean isSecure();
    Integer getVersion();
    Boolean getHttpOnly();
}
