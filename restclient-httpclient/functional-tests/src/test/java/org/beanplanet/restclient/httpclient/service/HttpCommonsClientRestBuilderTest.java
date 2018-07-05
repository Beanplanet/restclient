package org.beanplanet.restclient.httpclient.service;

import org.junit.Test;

import java.net.URI;
import java.net.URLEncoder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by gary on 01/08/2016.
 */
public class HttpCommonsClientRestBuilderTest {
    @Test
    public void pathParametersAreEncodedCorrectly() throws Exception {
        // Given
        HttpClientRestService service = new HttpClientRestService();
        HttpClientRestService.HttpCommonsClientRestBuilder restBuilder = service.createRestBuilder();
        String param1 = "crid://schange.com/e32bbcaa-7c69-4cda-8b31-681bda334a0d";

        // When
        restBuilder
                .baseUri("http://someHost/pathElem1/pathElem2/{param1}/{param2}/pathElem3")
                .pathParam("param1", param1)
                .pathParam("param2", "param2Value");


        //Then
        assertThat(restBuilder.getRequestUri(), is(new URI("http://someHost/pathElem1/pathElem2/" + URLEncoder.encode(param1) + "/param2Value/pathElem3")));
    }
}