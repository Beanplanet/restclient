package org.beanplanet.restclient.httpclient.functionaltests.entityprovider;

import org.beanplanet.restclient.httpclient.service.JacksonXmlEntityProvider;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Functional tests for XML entity provision and consumption.
 *
 * @author Gary Watson
 */
public class XMLEntityProvderTest {
    @Test
    public void canProduceXML() throws IOException {
        JacksonXmlEntityProvider entityFactory = new JacksonXmlEntityProvider();

        HttpMessage message = new HttpGet();
        message.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType());
        boolean canCreateEntity = entityFactory.canCreateEntityForObject(message, new Person());
        assertThat(canCreateEntity, is(true));

        HttpEntity entity = entityFactory.createEntityForObject(message, new Person());
        assertThat(entity, notNullValue());
        assertThat(EntityUtils.toString(entity), notNullValue());
    }

    @XmlRootElement(name="Person")
    public class Person {
        @XmlElement(name="Name")
        public String getName() {
            return "Fred";
        }
    }
}
