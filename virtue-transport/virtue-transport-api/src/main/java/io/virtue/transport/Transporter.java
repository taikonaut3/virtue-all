package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Transporter to Bind Server/Connect Client.
 */
@Extensible(NETTY)
public interface Transporter {

    AttributeKey<Transporter> ATTRIBUTE_KEY = AttributeKey.of(Key.TRANSPORTER);

    /**
     * Bind a Server to the URL with the given handler and the given codec.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     * @throws NetWorkException
     */
    Server bind(URL url, ChannelHandler handler, Codec codec) throws NetWorkException;

    /**
     * Connect to the server specified by the URL with the given handler and the given codec.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     * @throws NetWorkException
     */
    Client connect(URL url, ChannelHandler handler, Codec codec) throws NetWorkException;

}
