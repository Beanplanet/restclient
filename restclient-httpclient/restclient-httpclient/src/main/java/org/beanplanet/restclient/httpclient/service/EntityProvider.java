package org.beanplanet.restclient.httpclient.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;

/**
 * A factory for creating HTTP entities.
 *
 * @author Gary Watson
 */
public interface EntityProvider {
    /**
     * Determines whether the provider can produce an entity for the given HTTP Message, content type and object.
     *
     * @param message the HTTP message for which the entity is to be created.
     * @param object the object to be converted to an HTTP message entity.
     * @return false if this provider cannot produce an entity of the specified content type for the given object or true if the
     * provider may be able to produce an entity. If true, a subsequent call to {@link #createEntityForObject(HttpMessage, Object)} )} may be
     * made to attempt to produce an entity.
     */
    boolean canCreateEntityForObject(HttpMessage message, Object object) ;
    HttpEntity createEntityForObject(HttpMessage message, Object object) ;
    boolean canCreateObjectForEntity(HttpMessage message, HttpEntity entity, Class<?> clazz);
    <T> T createObjectForEntity(HttpMessage message, HttpEntity entity, Class<T> clazz);
}
