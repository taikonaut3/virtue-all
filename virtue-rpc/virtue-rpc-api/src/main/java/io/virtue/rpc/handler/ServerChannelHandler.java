package io.virtue.rpc.handler;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.rpc.event.RequestEvent;
import io.virtue.rpc.event.ServerHandlerExceptionEvent;
import io.virtue.transport.Request;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandlerAdapter;

/**
 * Server ChannelHandler.
 */
public final class ServerChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) {
        if (message instanceof Request request) {
            Virtue.ofServer(request.url())
                    .eventDispatcher()
                    .dispatch(new RequestEvent(request, channel));
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        URL url = channel.get(URL.ATTRIBUTE_KEY);
        if (url != null) {
            Virtue.ofServer(url)
                    .eventDispatcher()
                    .dispatch(new ServerHandlerExceptionEvent(channel, cause));
        }
    }
}
