package io.virtue.rpc.handler;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.rpc.event.ClientHandlerExceptionEvent;
import io.virtue.rpc.event.ResponseEvent;
import io.virtue.transport.Response;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandlerAdapter;

/**
 * Client ChannelHandler.
 */
public final class ClientChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Response response) {
            Virtue.ofClient(response.url())
                    .eventDispatcher()
                    .dispatch(new ResponseEvent(response));
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        URL url = channel.get(URL.ATTRIBUTE_KEY);
        if (url != null) {
            Virtue.ofClient(url)
                    .eventDispatcher()
                    .dispatch(new ClientHandlerExceptionEvent(channel, cause));
        }
    }

}
