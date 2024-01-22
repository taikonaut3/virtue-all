package io.github.astro.rpc.virtue.client;

import io.github.astro.rpc.event.ClientHandlerExceptionEvent;
import io.github.astro.rpc.handler.ClientChannelHandler;
import io.github.astro.rpc.handler.ClientHeartBeatChannelHandler;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.transport.base.DefaultChannelHandlerChain;
import io.github.astro.virtue.transport.channel.Channel;

/**
 * +------------------------------------------ClientChannelHandlerChain------------------------------------------+
 * +----------------------------------+----------------------------------------+---------------------------------+
 * |{@link ClientHeartBeatChannelHandler} -> {@link virtueClientConvertChannelHandler} -> {@link ClientChannelHandler} |
 * +----------------------------------+----------------------------------------+---------------------------------+
 */
public class VirtueClientChannelHandlerChain extends DefaultChannelHandlerChain {

    public VirtueClientChannelHandlerChain() {
        addLast(new ClientHeartBeatChannelHandler());
        addLast(new VirtueClientConvertChannelHandler());
        addLast(new ClientChannelHandler());
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        getEventDispatcher().dispatchEvent(new ClientHandlerExceptionEvent(channel, cause));
        super.caught(channel, cause);
    }

}
