package org.beanplanet.restclient.service;

import org.springframework.core.io.Resource;

/**
 * Defines the TLS/SSL restful configuration.
 *
 * @author Gary Watson
 */
public interface TlsConfiguration {
    TlsConfiguration trustStore(Resource keyStore, String keyStoreType, String password);
    TlsConfiguration clientKeyStore(Resource keyStore, String keyStoreType, String password);

    Resource getTrustStore();
    String getTrustStoreType();
    String getTrustStorePassword();

    Resource getClientKeyStore();
    String getClientKeyStoreType();
    String getClientKeyStorePassword();
}
