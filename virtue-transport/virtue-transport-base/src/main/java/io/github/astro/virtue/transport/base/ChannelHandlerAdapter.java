package io.github.astro.virtue.transport.base;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.transport.channel.Channel;
import io.github.astro.virtue.transport.channel.ChannelHandler;

public class ChannelHandlerAdapter implements ChannelHandler {

    protected Virtue virtue;

    protected ChannelHandlerAdapter() {
        this.virtue = Virtue.getDefault();
    }

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

    public EventDispatcher getEventDispatcher() {
        return virtue.eventDispatcher();
    }
}
