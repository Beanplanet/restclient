package org.beanplanet.restclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.beanplanet.core.net.http.HttpHeaders;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HttpBinAnythingResponse {
    private String method;
    private String url;
    private Map<String, String> args;
    private Map<String, String> headers;
    private String data;

    public HttpHeaders getHttpHeaders() {
        return HttpHeaders.builder().setAll(headers).build();
    }
}
