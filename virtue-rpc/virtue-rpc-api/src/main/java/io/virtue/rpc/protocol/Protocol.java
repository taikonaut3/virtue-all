package io.virtue.rpc.protocol;

import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Communication protocol used to exchange data between client and server.
 */
@Extensible(value = VIRTUE)
public interface Protocol extends ProtocolParser, ProtocolAdapter {

    /**
     * This protocol.
     *
     * @return
     */
    String protocol();

    /**
     * Send request.
     *
     * @param invocation
     * @return
     */
    RpcFuture sendRequest(Invocation invocation);

    /**
     * Send response.
     *
     * @param channel
     * @param invocation
     * @param result
     */
    void sendResponse(Channel channel, Invocation invocation, Object result);

    /**
     * A response is sent when an exception occurs in the Transport layer.
     *
     * @param channel
     * @param url
     * @param cause
     */
    void sendResponse(Channel channel, URL url, Throwable cause);

    /**
     * Opening a client may reuse the existing one.
     *
     * @param url client core {@link ClientConfig}
     * @return {@link Client}
     */
    Client openClient(URL url);

    /**
     * Opening a new Server.
     *
     * @param url server core {@link ServerConfig}
     * @return {@link Server}
     */
    Server openServer(URL url);

    /**
     * Get server Codec.
     *
     * @return {@link Codec}
     */
    Codec serverCodec();

    /**
     * Get client Codec.
     *
     * @return {@link Codec}
     */
    Codec clientCodec();

}
