package io.virtue.transport.channel;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.Accessor;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.extension.resoruce.Closeable;

import java.net.InetSocketAddress;

/**
 * Net channel interface that extends the Endpoint and Closeable interfaces.
 */
public interface Channel extends Accessor, Closeable {

    AttributeKey<Channel> ATTRIBUTE_KEY = AttributeKey.of("channel");

    /**
     * Returns the local address associated with the channel.
     *
     * @return
     */
    InetSocketAddress localAddress();

    /**
     * Returns the remote address associated with the channel.
     *
     * @return
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
