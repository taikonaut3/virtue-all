package io.virtue.transport.http.h2;

import io.virtue.common.spi.Extensible;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.client.Client;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Http2StreamSender.
 */
@Extensible(NETTY)
public interface Http2StreamSender {

    /**
     * Sends http2 request.
     *
     * @param client
     * @param request
     * @param future
     */
    void send(Client client, Http2Request request, RpcFuture future);

    /**
     * Sends http2 response.
     *
     * @param channel
     * @param response
     */
    void send(Channel channel, Http2Response response);
}
