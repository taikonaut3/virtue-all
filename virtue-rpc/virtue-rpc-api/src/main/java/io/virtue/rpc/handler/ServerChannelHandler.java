package io.virtue.rpc.handler;

import io.virtue.rpc.event.RequestEvent;
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
            getEventDispatcher(request.url()).dispatchEvent(new RequestEvent(request, channel));
        }
    }

}
