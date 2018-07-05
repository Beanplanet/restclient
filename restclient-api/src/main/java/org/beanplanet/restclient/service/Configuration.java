package org.beanplanet.restclient.service;

/**
 * Top-level configuration of the REST service.
 *
 * @author Gary Watson
 */
public interface Configuration {
    /**
     * Gets the current TLS/SSL Configuration.
     *
     * @return the TLS/SSL configuration
     */
    TlsConfiguration tlsConfig();
    Configuration tlsConfig(TlsConfiguration tlsConfiguration);
    ConnectionPoolConfiguration poolConfig();
    Configuration poolConfig(ConnectionPoolConfiguration connectionPoolConfiguration);
}
