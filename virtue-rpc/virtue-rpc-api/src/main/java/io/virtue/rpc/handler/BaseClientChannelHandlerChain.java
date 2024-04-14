package io.virtue.rpc.handler;

/**
 * VirtueClientChannelHandlerChain.
 */
public class BaseClientChannelHandlerChain extends DefaultChannelHandlerChain {

    public BaseClientChannelHandlerChain() {
        addLast(new ClientChannelHandler());
    }

}
