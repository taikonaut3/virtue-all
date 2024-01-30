package io.github.astro.virtue.rpc.protocol;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Communication protocol used to exchange data between client and server.
 */
@ServiceInterface(VIRTUE)
public interface Protocol<Req, Res> {

    /**
     *  This protocol.
     *
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

    Client openClient(URL url);

    Server openServer(URL url);

    Codec serverCodec();

    Codec clientCodec();

    ProtocolParser parser();

}
