package io.virtue.rpc.protocol;

import io.virtue.common.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.InvokerFactory;
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
 *
 * @param <Req>  Request type
 * @param <Resp> Response type
 */
@Extensible(value = VIRTUE)
public interface Protocol {

    /**
     * This protocol.
     *
     * @return
     */
    String protocol();

    /**
     * Invoker factory.
     *
     * @return
     */
    InvokerFactory invokerFactory();

    default RpcFuture sendRequest(Invocation invocation) {
        return null;
    }

    default void sendResponse(Channel channel, Invocation invocation, Object result) {

    }

    default void sendResponse(Channel channel, URL url, Throwable cause) {

    }

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

    /**
     * Get ProtocolParser.
     *
     * @return {@link ProtocolParser}
     */
    ProtocolParser parser();

}
