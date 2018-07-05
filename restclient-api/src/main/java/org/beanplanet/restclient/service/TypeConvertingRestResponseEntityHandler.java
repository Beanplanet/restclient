package org.beanplanet.restclient.service;

/**
 * An extremly useful REST response handler which converts the returned entity to a given POJO.
 *
 * @author Gary Watson
 */
public class TypeConvertingRestResponseEntityHandler<T> implements RestResponseHandler<T> {
    /** The type the REST response entity will be converted to. */
    private Class<?> responseType;
    /** The status code expected to be returned by the REST response. */
    private Integer expectedHttpResponseCode;

    /**
     * Constructs a response handler which converts the entity returned in responses to the specified type. This
     * instance will not check the status code returned in the response.
     *
     * @param responseType the type the REST response entity will be converted to.
     */
    public TypeConvertingRestResponseEntityHandler(Class<?> responseType) {
        this.responseType = responseType;
    }

    /**
     * Constructs a response handler which first ensures the status code returned is as expected and the
     * converts the entity returned in responses to the specified type.
     *
     * @param expectedHttpResponseCode the status code expected to be returned by the REST response.
     * @param responseType the type the REST response entity will be converted to.
     */
    public TypeConvertingRestResponseEntityHandler(int expectedHttpResponseCode, Class<?> responseType) {
        this.expectedHttpResponseCode = expectedHttpResponseCode;
        this.responseType = responseType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T handleResponse(RestResponse response) {
        if (expectedHttpResponseCode != null && expectedHttpResponseCode != response.getStatusCode()) {
            throw new IllegalStateException(String.format("Expecting HTTP status code %d and received actual status code of %d", expectedHttpResponseCode, response.getStatusCode()));
        }

        return (T)response.getEntity(responseType);
    }

    /**
     * Gets the type the REST response entity will be converted to.
     *
     * @return the type the REST response entity will be converted to.
     */
    public Class<?> getResponseType() {
        return responseType;
    }

    /**
     * Sets the type the REST response entity will be converted to.
     *
     * @param responseType the type the REST response entity will be converted to.
     */
    public void setResponseType(Class<?> responseType) {
        this.responseType = responseType;
    }

    /**
     * Gets the status code expected to be returned by the REST response.
     *
     * @return the status code expected to be returned by the REST response.
     */
    public Integer getExpectedHttpResponseCode() {
        return expectedHttpResponseCode;
    }

    /**
     * Sets the status code expected to be returned by the REST response.
     *
     * @param expectedHttpResponseCode the status code expected to be returned by the REST response.
     */
    public void setExpectedHttpResponseCode(Integer expectedHttpResponseCode) {
        this.expectedHttpResponseCode = expectedHttpResponseCode;
    }
}
