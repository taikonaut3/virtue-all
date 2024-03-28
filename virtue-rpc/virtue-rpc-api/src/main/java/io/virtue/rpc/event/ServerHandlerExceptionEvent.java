package io.virtue.rpc.event;

import io.virtue.transport.channel.Channel;

/**
 * ServerHandler ExceptionEvent.
 */
public class ServerHandlerExceptionEvent extends ChannelHandlerExceptionEvent {

    public ServerHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(channel, cause);
    }

}
