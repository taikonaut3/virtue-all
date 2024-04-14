package io.virtue.rpc.handler;


/**
 * +---------------------------------------ServerChannelHandlerChain---------------------------------------+
 * +--------------------------------+--------------------------------------+-------------------------------+
 * |{@link ServerHeartBeatChannelHandler} -> {@link virtueServerConvertChannelHandler} -> {@link ServerChannelHandler} |
 * +--------------------------------+--------------------------------------+-------------------------------+.
 */
public class BaseServerChannelHandlerChain extends DefaultChannelHandlerChain {

    public BaseServerChannelHandlerChain() {
        addLast(new ServerChannelHandler());
    }
}
