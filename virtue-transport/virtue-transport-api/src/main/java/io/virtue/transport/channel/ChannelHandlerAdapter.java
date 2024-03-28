package io.virtue.transport.channel;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.event.EventDispatcher;

/**
 * ChannelHandlerAdapter.
 */
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

    /**
     * Get the event dispatcher.
     * @param url
     * @return
     */
    public EventDispatcher getEventDispatcher(URL url) {
        Virtue virtue = Virtue.get(url);
        return virtue.eventDispatcher();
    }
}
