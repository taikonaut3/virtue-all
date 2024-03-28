package io.virtue.rpc.event;

import io.virtue.transport.channel.Channel;

/**
 * ClientHandlerExceptionEvent.
 */
public class ClientHandlerExceptionEvent extends ChannelHandlerExceptionEvent {

    public ClientHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(channel, cause);
    }

}
