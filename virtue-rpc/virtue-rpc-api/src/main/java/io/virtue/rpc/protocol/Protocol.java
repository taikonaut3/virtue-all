package io.virtue.rpc.protocol;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.Virtue;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Communication protocol used to exchange data between client and server.
 */
@ServiceInterface(value = VIRTUE, constructor = {Virtue.class})
public interface Protocol<Req, Res> {

    /**
     *  This protocol.
     */
    String protocol();

    /**
     * Create a new request for the given URL and message body.
     *
     * @param url     the URL to which the request is sent
     * @param payload the message payload of the request
     * @return {@link Request}
     */
    Req createRequest(Invocation invocation);

    /**
     * Create a new response for the given URL and message body.
     *
     * @param url     {@link URL}
     * @param payload the message payload of the response
     * @return {@link Response}
     */
    Res createResponse(URL url, Object payload);

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
