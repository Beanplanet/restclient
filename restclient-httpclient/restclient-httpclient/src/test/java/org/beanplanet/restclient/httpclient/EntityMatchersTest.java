package org.beanplanet.restclient.httpclient;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.beanplanet.restclient.httpclient.EntityMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link EntityMatchers}.
 *
 * @author Gary Watson
 */
public class EntityMatchersTest {
    @Test
    public void mimeTypesMatches() {
        HttpGet message = new HttpGet();
        message.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        assertThat(EntityMatchers.mimeTypes("application/json").evaluate(message), is(true));
        assertThat(EntityMatchers.mimeTypes(".*json").evaluate(message), is(true));
        assertThat(EntityMatchers.mimeTypes("application/json", "some/other").evaluate(message), is(true));
        assertThat(EntityMatchers.mimeTypes("some/other","application/json").evaluate(message), is(true));
    }

    @Test
    public void mimeTypesDoNotMatch() {
        HttpGet message = new HttpGet();
        message.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM.getMimeType());

        assertThat(EntityMatchers.mimeTypes("text/json").evaluate(message), is(false));
        assertThat(EntityMatchers.mimeTypes(".*json").evaluate(message), is(false));
        assertThat(EntityMatchers.mimeTypes("application/json", "some/other").evaluate(message), is(false));
        assertThat(EntityMatchers.mimeTypes("some/other","application/json").evaluate(message), is(false));
    }
}