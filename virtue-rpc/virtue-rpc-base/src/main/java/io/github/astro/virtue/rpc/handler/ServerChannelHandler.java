package io.github.astro.virtue.rpc.handler;

import io.github.astro.virtue.rpc.event.RequestEvent;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.base.ChannelHandlerAdapter;
import io.github.astro.virtue.transport.channel.Channel;

public final class ServerChannelHandler extends ChannelHandlerAdapter {


    @Override
    public void received(Channel channel, Object message) {
        if (message instanceof Request request) {
            getEventDispatcher().dispatchEvent(new RequestEvent(request, channel));
        }
    }

}
