package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class MultiValueMapSerialiser extends JsonSerializer<MultiValuedMap> {
    public Class<MultiValuedMap> handledType() {
        return MultiValuedMap.class;
    }

    @Override
    public void serialize(MultiValuedMap value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (null == value) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeStartObject();

            Map<String, Collection<?>> map = value.asMap();
            for (Map.Entry<String, Collection<?>> entry : map.entrySet()) {
                Object entryValue;
                if (entry.getValue() == null) {
                    entryValue = null;
                } else if (entry.getValue().size() == 1) {
                    entryValue = entry.getValue().iterator().next();
                } else {
                    entryValue = entry.getValue();
                }

                jsonGenerator.writeObjectField(entry.getKey(), entryValue);
            }

            jsonGenerator.writeEndObject();

//
//            for(String fieldName : value.keySet()) {
//                jsonGenerator.writeObjectField(fieldName, value.);
//
//                if (value.get(entry.getKey()) == null) {
//                    jsonGenerator.writeNull();
//                } else {
//                    jsonGenerator.writeStartArray();
//
//                    for (Object fieldValue : value.get(entry.getKey())) {
//
//                    }
//
//                    jsonGenerator.writeEndArray();
//                }
//            }
//
//            jsonGenerator.writeEndObject();
//            final DecimalFormat myFormatter = new DecimalFormat("0.000");
//            final String output = myFormatter.format(value);
//            jsonGenerator.writeNumber(output);
        }
    }
}
