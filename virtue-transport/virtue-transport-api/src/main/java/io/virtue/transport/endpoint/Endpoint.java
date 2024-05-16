package io.virtue.transport.endpoint;

import io.virtue.common.extension.resoruce.Closeable;
import io.virtue.common.util.NetUtil;

import java.net.InetSocketAddress;

/**
 * Network endpoint,including Host and Port information.
 */
public interface Endpoint extends Closeable {

    /**
     * Get the host name of the endpoint.
     *
     * @return host
     */
    String host();

    /**
     * Get the port number of the endpoint.
     *
     * @return port
     */
    int port();

    /**
     * Convert the endpoint to an InetSocketAddress object.
     *
     * @return InetSocketAddress instance
     */
    InetSocketAddress inetSocketAddress();

    /**
     * Convert the endpoint to "host:port".
     *
     * @return address
     */
    default String address() {
        return NetUtil.getAddress(host(), port());
    }

}