package org.beanplanet.restclient.domain.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * A model of an HTTP response.
 *
 * @author Gary Watson
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "statusCode",
        "reasonPhrase",
        "headers",
        "entity"
})
public class HttpResponse extends HttpMessage {
    /** The HTTP response status code. */
    private int statusCode;
    /** The phrase associated with the given status code. */
    private String reasonPhrase;


    public HttpResponse() {
    }

    public HttpResponse(int status, MultiValuedMap<String, String> headers) {
        this.statusCode = status;
        setHeaders(headers);
    }

    public HttpResponse(HttpServletResponse servletResponse) {
// GAW 2016-06-21 Unable to use Servlet 3.0 on the classpath locally so getHeaderNames() and getStatus() not present in HttpServletResponse in 2.5
//        MultiValuedMap<String, String> headers = new ArrayListValuedHashMap<>();
//        for (String headerName : IterableUtil.nullSafe(servletResponse.getHeaderNames())) {
//            headers.putAll(headerName, servletResponse.getHeaders(headerName));
//        }
//        setHeaders(headers);
//
//        this.statusCode = servletResponse.getStatus();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String toString() {
        return new ReflectionToStringBuilder(this).build();
    }
}
