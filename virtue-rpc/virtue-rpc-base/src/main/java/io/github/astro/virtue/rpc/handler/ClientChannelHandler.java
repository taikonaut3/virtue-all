package io.github.astro.virtue.rpc.handler;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.rpc.event.ResponseEvent;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.base.ChannelHandlerAdapter;
import io.github.astro.virtue.transport.channel.Channel;

public final class ClientChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Response response) {
            getEventDispatcher(response.url()).dispatchEvent(new ResponseEvent(response));
        }
    }

}
