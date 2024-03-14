package io.virtue.rpc.virtue.server;

import io.virtue.rpc.handler.DefaultChannelHandlerChain;
import io.virtue.rpc.handler.ServerChannelHandler;
import io.virtue.rpc.handler.ServerHeartBeatChannelHandler;

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
