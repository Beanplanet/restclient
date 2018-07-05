package org.beanplanet.restclient.service;

/**
 * A {@link RestResponseHandler} which simply returns the rest response object to the caller. Note, in
 * such circumstances the caller would then be required to close the response via a call to {@link RestResponse#close()}
 * or {@link RestResponse#closeQuietly()}.
 *
 * <p>Usage is of the form:
 *
 * <pre>
 *     RestResponse response = restService.get(new RestResponseReturningHandler());
 *     int httpStatusCode = response.
 * </pre>
 * </p>
 *
 * @author Gary Watson
 */
public class RestResponseReturningHandler implements RestResponseHandler<RestResponse> {
    /**
     * Returns the rest response to the caller. The caller is then responsible for closing the response object to
     * ensure system resources are freed.
     *
     * @param response the response object. representing the HTTP response returned from the call.
     * @return the response object.
     */
    @Override
    public RestResponse handleResponse(RestResponse response) {
        return response;
    }
}
