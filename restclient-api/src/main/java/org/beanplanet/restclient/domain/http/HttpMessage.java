package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * A model of an HTTP message, common to both request and response.
 *
 * @author Gary Watson
 */
public class HttpMessage {
    /** The headers associated with the message. */
    @JsonSerialize(using = MultiValueMapSerialiser.class)
    @JsonDeserialize(as = ArrayListValuedHashMap.class, using = MultiValueMapDeserialiser.class)
    private MultiValuedMap<String, String> headers;
    /** The entity associated with the message. */
    private Resource entity;

    /**
     * Gets the headers associated with the message.
     *
     * @return the headers associated with the message.
     */
    public MultiValuedMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers associated with the message.
     *
     * @param headers the headers associated with the message.
     */
    public void setHeaders(MultiValuedMap<String, String> headers) {
        this.headers = headers;
    }

    public HttpMessage withHeaders(Map<String, Object> headers) {
        if (headers == null) return null;

        for (Map.Entry<String, Object> header : headers.entrySet()) {
            withHeader(header.getKey(), String.valueOf(header.getValue()));
        }

        return this;
    }

    public HttpMessage withHeader(String name, String value) {
        if (headers == null) {
            this.headers = new ArrayListValuedHashMap<>();
        }

        headers.put(name, value);
        return this;
    }

    /**
     * Gets the entity associated with the message.
     *
     * @return the entity associated with the message.
     */
    public Resource getEntity() {
        return entity;
    }

    /**
     * Sets the entity associated with the message.
     *
     * @param entity the entity associated with the message.
     */
    public void setEntity(Resource entity) {
        this.entity = entity;
    }

    /**
     * Sets the entity associated with the message.
     *
     * @param entity the entity associated with the message.
     * @return this instance for method chaining.
     *
     */
    public HttpMessage withEntity(Resource entity) {
        setEntity(entity);
        return this;
    }
}
