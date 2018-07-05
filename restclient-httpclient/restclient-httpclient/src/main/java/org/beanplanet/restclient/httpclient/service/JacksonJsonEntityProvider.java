package org.beanplanet.restclient.httpclient.service;

import org.beanplanet.restclient.httpclient.EntityMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * A factory for creating HTTP entity bodies using the Jackson JSON library.
 *
 * @author Gary Watson
 */
public class JacksonJsonEntityProvider extends AbstractRuleMatchingEntityProvider {
    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JacksonJsonEntityProvider() {
        super(EntityMatchers.mimeTypes(ContentType.APPLICATION_JSON.getMimeType()));
    }

    public HttpEntity createEntityForObject(HttpMessage message, Object object) {
        try {
            String stringEntity = (object instanceof CharSequence ? object.toString() : objectMapper.writeValueAsString(object));
            Header contentTypeHeader = message.getLastHeader(HttpHeaders.CONTENT_TYPE);

            return contentTypeHeader != null ? new StringEntity(stringEntity, ContentType.parse(contentTypeHeader.getValue())) : new StringEntity(stringEntity);
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    public <T> T createObjectForEntity(HttpMessage message, HttpEntity entity, Class<T> clazz) {
        try {
            String entityString = EntityUtils.toString(entity, "UTF-8");
            return clazz.isAssignableFrom(String.class) ? (T)entityString : objectMapper.readValue(entityString, clazz);
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }
}
