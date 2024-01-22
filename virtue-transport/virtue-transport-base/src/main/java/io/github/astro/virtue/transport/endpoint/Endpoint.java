package io.github.astro.virtue.transport.endpoint;

import io.github.astro.virtue.common.util.NetUtil;

import java.net.InetSocketAddress;

/**
 * Network endpoint,including Host and Port information.
 */
public interface Endpoint {

    /**
     * Get the host name of the endpoint.
     *
     * @return
     */
    String host();

    /**
     * Get the port number of the endpoint.
     *
     * @return
     */
    int port();

    /**
     * Convert the endpoint to an InetSocketAddress object.
     *
     * @return
     */
    InetSocketAddress toInetSocketAddress();

    /**
     * Convert the endpoint to "host:port".
     *
     * @return
     */
    default String address() {
        return NetUtil.getAddress(host(), port());
    }

}