package org.beanplanet.restclient.httpclient.service;

import org.beanplanet.restclient.service.TlsConfiguration;
import org.springframework.core.io.Resource;

public class HttpTlsConfiguration implements TlsConfiguration{
    private Resource trustStore;
    private String trustStoreType;
    private String trustStorePassword;

    private Resource clientKeyStore;
    private String clientKeyStoreType;
    private String clientKeyStorePassword;

    public static HttpTlsConfiguration tlsConfig() { return new HttpTlsConfiguration(); }

    @Override
    public HttpTlsConfiguration trustStore(Resource keyStore, String keyStoreType, String password) {
        this.trustStore = keyStore;
        this.trustStoreType = keyStoreType;
        this.trustStorePassword = password;
        return this;
    }

    @Override
    public HttpTlsConfiguration clientKeyStore(Resource keyStore, String keyStoreType, String password) {
        this.clientKeyStore = keyStore;
        this.clientKeyStoreType = keyStoreType;
        this.clientKeyStorePassword = password;
        return this;
    }

    public Resource getTrustStore() {
        return trustStore;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public Resource getClientKeyStore() {
        return clientKeyStore;
    }

    public String getClientKeyStoreType() {
        return clientKeyStoreType;
    }

    public String getClientKeyStorePassword() {
        return clientKeyStorePassword;
    }
}
