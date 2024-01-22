package io.github.astro.virtue.transport.channel;

import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.transport.Closeable;
import io.github.astro.virtue.transport.endpoint.Endpoint;

import java.net.InetSocketAddress;

/**
 * Net channel interface that extends the Endpoint and Closeable interfaces.
 */
public interface Channel extends Endpoint, Closeable {

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

    /**
     * Returns the attribute value associated with the specified key.
     *
     * @param key The key of the attribute.
     * @return The attribute value associated with the specified key.
     */
    Object getAttribute(String key);

    /**
     * Returns the attribute value associated with the specified key. If the value is null, returns the default value and sets it.
     *
     * @param key          The key of the attribute.
     * @param defaultValue The default value to return if the attribute value is null.
     * @return The attribute value associated with the specified key.
     */
    Object getAttribute(String key, Object defaultValue);

    /**
     * Sets the attribute value associated with the specified key.
     *
     * @param key   The key of the attribute.
     * @param value The value to set for the attribute.
     */
    void setAttribute(String key, Object value);

    /**
     * Removes the attribute associated with the specified key.
     *
     * @param key The key of the attribute to remove.
     */
    void removeAttribute(String key);

}
