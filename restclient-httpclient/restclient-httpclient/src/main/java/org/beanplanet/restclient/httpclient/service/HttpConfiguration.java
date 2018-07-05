package org.beanplanet.restclient.httpclient.service;

import org.beanplanet.restclient.service.Configuration;
import org.beanplanet.restclient.service.ConnectionPoolConfiguration;
import org.beanplanet.restclient.service.TlsConfiguration;

/**
 * Implementation of the top-level REST service configuration.
 *
 * @author Gary Watson
 */
public class HttpConfiguration implements Configuration {
    private TlsConfiguration        tlsConfiguration;
    private ConnectionPoolConfiguration poolConfiguration;

    public static HttpConfiguration httpConfig() { return new HttpConfiguration(); }

    public static HttpConfiguration httpConfig(TlsConfiguration tlsConfiguration) {
        return httpConfig().tlsConfig(tlsConfiguration);
    }

    public static HttpConfiguration httpConfig(ConnectionPoolConfiguration connectionPoolConfiguration) {
        return httpConfig().poolConfig(connectionPoolConfiguration);
    }

    public static HttpConfiguration httpConfig(TlsConfiguration tlsConfiguration, ConnectionPoolConfiguration connectionPoolConfiguration) {
        return httpConfig()
                .tlsConfig(tlsConfiguration)
                .poolConfig(connectionPoolConfiguration);
    }

    /**
     * Gets the current TLS/SSL Configuration.
     *
     * @return the TLS/SSL configuration
     */
    @Override
    public TlsConfiguration tlsConfig() {
        return tlsConfiguration;
    }

    @Override
    public HttpConfiguration tlsConfig(TlsConfiguration tlsConfiguration) {
        this.tlsConfiguration = tlsConfiguration;
        return this;
    }

    @Override
    public ConnectionPoolConfiguration poolConfig() {
        return poolConfiguration;
    }

    @Override
    public HttpConfiguration poolConfig(ConnectionPoolConfiguration connectionPoolConfiguration) {
        this.poolConfiguration = connectionPoolConfiguration;
        return this;
    }
}
