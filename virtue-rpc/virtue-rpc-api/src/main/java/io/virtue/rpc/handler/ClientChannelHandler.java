package io.virtue.rpc.handler;

import io.virtue.common.exception.RpcException;
import io.virtue.rpc.event.ResponseEvent;
import io.virtue.transport.Response;
import io.virtue.transport.channel.ChannelHandlerAdapter;
import io.virtue.transport.channel.Channel;

public final class ClientChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Response response) {
            getEventDispatcher(response.url()).dispatchEvent(new ResponseEvent(response));
        }
    }

}
