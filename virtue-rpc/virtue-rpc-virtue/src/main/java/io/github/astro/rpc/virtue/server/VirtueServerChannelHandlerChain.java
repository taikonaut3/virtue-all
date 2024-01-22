package io.github.astro.rpc.virtue.server;

import io.github.astro.rpc.event.ServerHandlerExceptionEvent;
import io.github.astro.rpc.handler.ServerChannelHandler;
import io.github.astro.rpc.handler.ServerHeartBeatChannelHandler;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.transport.base.DefaultChannelHandlerChain;
import io.github.astro.virtue.transport.channel.Channel;

/**
 * +---------------------------------------ServerChannelHandlerChain---------------------------------------+
 * +--------------------------------+--------------------------------------+-------------------------------+
 * |{@link ServerHeartBeatChannelHandler} -> {@link virtueServerConvertChannelHandler} -> {@link ServerChannelHandler} |
 * +--------------------------------+--------------------------------------+-------------------------------+
 */
public class VirtueServerChannelHandlerChain extends DefaultChannelHandlerChain {

    public VirtueServerChannelHandlerChain() {
        addLast(new ServerHeartBeatChannelHandler());
        addLast(new VirtueServerConvertChannelHandler());
        addLast(new ServerChannelHandler());
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        getEventDispatcher().dispatchEvent(new ServerHandlerExceptionEvent(channel, cause));
        super.caught(channel, cause);
    }

}
