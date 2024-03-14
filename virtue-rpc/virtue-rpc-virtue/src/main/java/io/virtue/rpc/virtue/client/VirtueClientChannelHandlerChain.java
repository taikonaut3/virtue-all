package io.virtue.rpc.virtue.client;

import io.virtue.rpc.handler.ClientChannelHandler;
import io.virtue.rpc.handler.ClientHeartBeatChannelHandler;
import io.virtue.rpc.handler.DefaultChannelHandlerChain;

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

}
