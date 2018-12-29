package org.beanplanet.restclient.httpclient.service;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.beanplanet.restclient.httpclient.EntityMatchers;

import java.io.IOException;

/**
 * A factory for creating HTTP entity bodies using the Jackson JSON library.
 *
 * @author Gary Watson
 */
public class CharsequenceEntityProvider extends AbstractRuleMatchingEntityProvider {
    public CharsequenceEntityProvider() {
        super(EntityMatchers.mimeTypes("text/.*"));
    }

    public HttpEntity createEntityForObject(HttpMessage message, Object object) {
        try {
            String stringEntity = object == null ? "" : String.valueOf(object);
            Header contentTypeHeader = message.getLastHeader(HttpHeaders.CONTENT_TYPE);

            return contentTypeHeader != null ? new StringEntity(stringEntity, ContentType.parse(contentTypeHeader.getValue())) : new StringEntity(stringEntity);
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    public <T> T createObjectForEntity(HttpMessage message, HttpEntity entity, Class<T> clazz) {
        if ( !CharSequence.class.isAssignableFrom(clazz) ) return null;

        try {
            return (T)EntityUtils.toString(entity, "UTF-8");
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }
}
