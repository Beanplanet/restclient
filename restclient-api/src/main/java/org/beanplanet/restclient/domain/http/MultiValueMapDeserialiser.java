package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.beanplanet.core.util.MultiValueMap;
import org.beanplanet.core.util.MultiValueMapImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gary on 04/05/2016.
 */
public class MultiValueMapDeserialiser extends JsonDeserializer<MultiValueMap> {
    @Override
    @SuppressWarnings("unchecked")
    public MultiValueMap deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map map = jsonParser.readValueAs(Map.class);
        return new MultiValueMapImpl(map);
    }
}
