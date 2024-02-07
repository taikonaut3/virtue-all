package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.common.extension.AttributeKey;
import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;

import static io.github.astro.virtue.common.constant.Components.Transport.NETTY;

/**
 * Transporter to Bind Server/Connect Client
 */
@ServiceInterface(NETTY)
public interface Transporter {

    AttributeKey<Transporter> ATTRIBUTE_KEY = AttributeKey.get(Key.TRANSPORTER);

    /**
     * Binds a Server to the URL with the given codec.
     *
     * @param url   The URL to bind the server and increase extensibility.
     * @param codec The codec to be used for message encoding and decoding.
     * @return The bound server object.
     * @throws NetWorkException
     */
    Server bind(URL url, ChannelHandler handler, Codec codec) throws NetWorkException;

    /**
     * Connects to the server specified by the URL with the given codec.
     *
     * @param url   The URL of the server to connect and Increase extensibility.
     * @param codec The codec to be used for message encoding and decoding.
     * @return The connected client object.
     * @throws NetWorkException
     */
    Client connect(URL url, ChannelHandler handler, Codec codec) throws NetWorkException;

}
