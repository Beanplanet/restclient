package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.beanplanet.core.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MultiValueMapSerialiser extends JsonSerializer<MultiValueMap> {
    public Class<MultiValueMap> handledType() {
        return MultiValueMap.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(MultiValueMap value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (null == value) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeStartObject();

            for (Map.Entry entry : (Set<Map.Entry>) value.entrySet()) {
                jsonGenerator.writeObjectField(String.valueOf(entry.getKey()), entry.getValue());
            }

            jsonGenerator.writeEndObject();
        }
    }
}
