package io.virtue.transport.channel;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.Accessor;
import io.virtue.config.Closeable;
import io.virtue.transport.endpoint.Endpoint;

import java.net.InetSocketAddress;

/**
 * Net channel interface that extends the Endpoint and Closeable interfaces.
 */
public interface Channel extends Endpoint, Accessor, Closeable {

    /**
     * Returns the remote address associated with the channel.
     *
     * @return The remote address associated with the channel.
     */
    InetSocketAddress remoteAddress();

    /**
     * Sends the given message through the channel.
     *
     * @param message The message to send.
     * @throws NetWorkException if an error occurs during network communication.
     */
    void send(Object message) throws NetWorkException;

}
