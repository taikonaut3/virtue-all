package io.virtue.transport.channel;

import io.virtue.common.exception.RpcException;

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
}
