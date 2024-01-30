package io.github.astro.virtue.rpc.virtue.server;

import io.github.astro.virtue.rpc.handler.DefaultChannelHandlerChain;
import io.github.astro.virtue.rpc.handler.ServerChannelHandler;
import io.github.astro.virtue.rpc.handler.ServerHeartBeatChannelHandler;

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
}
