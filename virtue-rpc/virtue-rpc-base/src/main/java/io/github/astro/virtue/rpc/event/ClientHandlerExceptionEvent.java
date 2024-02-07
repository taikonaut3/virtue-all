package io.github.astro.virtue.rpc.event;

import io.github.astro.virtue.transport.channel.Channel;

public class ClientHandlerExceptionEvent extends ChannelHandlerExceptionEvent {

    public ClientHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(channel, cause);
    }

}
