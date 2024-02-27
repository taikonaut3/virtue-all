package io.github.taikonaut3.virtue.rpc.protocol;

import io.github.taikonaut3.virtue.common.spi.ServiceInterface;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.config.CallArgs;
import io.github.taikonaut3.virtue.transport.Request;
import io.github.taikonaut3.virtue.transport.Response;
import io.github.taikonaut3.virtue.transport.client.Client;
import io.github.taikonaut3.virtue.transport.codec.Codec;
import io.github.taikonaut3.virtue.transport.server.Server;

import static io.github.taikonaut3.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Communication protocol used to exchange data between client and server.
 */
@ServiceInterface(VIRTUE)
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
    Req createRequest(URL url, CallArgs args);

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
     * @param url client config {@link io.github.taikonaut3.virtue.config.config.ClientConfig}
     * @return {@link Client}
     */
    Client openClient(URL url);

    /**
     * Opening a new Server.
     *
     * @param url server config {@link io.github.taikonaut3.virtue.config.config.ServerConfig}
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
