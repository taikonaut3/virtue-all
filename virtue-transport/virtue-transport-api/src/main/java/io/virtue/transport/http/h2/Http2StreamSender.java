package io.virtue.transport.http.h2;

import io.virtue.common.spi.Extensible;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Http2 stream message sender.
 * <p>HTTP 2 messages are sent in the form of frames,it can be multiple frames,then it needs to be sent more than once</p>
 */
@Extensible(NETTY)
public interface Http2StreamSender {

    /**
     * Sends http2 request.
     *
     * @param future associate http2 stream
     * @param request
     */
    void send(RpcFuture future, Http2Request request);

    /**
     * Sends http2 response.
     *
     * @param channel
     * @param response
     */
    void send(Channel channel, Http2Response response);
}
