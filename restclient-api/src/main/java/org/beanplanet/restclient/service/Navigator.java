package org.beanplanet.restclient.service;

import java.util.List;
import java.util.Map;

public interface Navigator {
    Boolean getBoolean(String path) throws NavigationException;
    Number getNumber(String path) throws NavigationException;
    String getString(String path) throws NavigationException;

    <T> List<T> getList(String path) throws NavigationException;
    Map getMap(String path) throws NavigationException;
}
