package io.virtue.rpc.virtue.client;

import io.virtue.rpc.handler.ClientChannelHandler;
import io.virtue.rpc.handler.ClientHeartBeatChannelHandler;
import io.virtue.rpc.handler.DefaultChannelHandlerChain;

/**
 * VirtueClientChannelHandlerChain.
 */
public class VirtueClientChannelHandlerChain extends DefaultChannelHandlerChain {

    public VirtueClientChannelHandlerChain() {
        addLast(new ClientHeartBeatChannelHandler());
        addLast(new ClientChannelHandler());
    }

}
