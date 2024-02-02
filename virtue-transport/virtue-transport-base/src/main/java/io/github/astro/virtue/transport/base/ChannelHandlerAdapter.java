package io.github.astro.virtue.transport.base;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.transport.channel.Channel;
import io.github.astro.virtue.transport.channel.ChannelHandler;

public class ChannelHandlerAdapter implements ChannelHandler {



    @Override
    public void connected(Channel channel) throws RpcException {

    }

    @Override
    public void disconnected(Channel channel) throws RpcException {

    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {

    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {

    }

    @Override
    public Channel[] getChannels() {
        return new Channel[0];
    }

    public EventDispatcher getEventDispatcher(URL url) {
        Virtue virtue = Virtue.get(url);
        return virtue.eventDispatcher();
    }
}
