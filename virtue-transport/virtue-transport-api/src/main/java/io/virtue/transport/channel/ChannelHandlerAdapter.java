package io.virtue.transport.channel;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.manager.Virtue;
import io.virtue.event.EventDispatcher;

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
