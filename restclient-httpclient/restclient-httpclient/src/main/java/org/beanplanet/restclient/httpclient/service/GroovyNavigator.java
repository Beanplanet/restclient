package org.beanplanet.restclient.httpclient.service;

import org.beanplanet.restclient.service.NavigationException;
import org.beanplanet.restclient.service.Navigator;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroovyNavigator implements Navigator {
    private GroovyShell groovyShell;

    public GroovyNavigator(Object binding) {
        Map<String, Object> groovyBinding = new HashMap<>();
        groovyBinding.put("context", binding);
        groovyShell = new GroovyShell(new Binding(groovyBinding));
    }

    @Override
    public Boolean getBoolean(String path) throws NavigationException {
        return (Boolean)groovyShell.evaluate("context."+path+" as Boolean");
    }

    @Override
    public Number getNumber(String path) throws NavigationException {
        return (Number)groovyShell.evaluate("context."+path+" as Number");
    }

    @Override
    public String getString(String path) throws NavigationException {
        return String.valueOf(groovyShell.evaluate("context." + path));
    }

    @Override
    public <T> List<T> getList(String path) {
        return (List<T>)groovyShell.evaluate("context."+path+" as List");
    }

    @Override
    public Map getMap(String path) {
        return (Map)groovyShell.evaluate("context."+path+" as Map");
    }
}
