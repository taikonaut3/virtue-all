package io.github.astro.virtue.rpc.event;

import io.github.astro.virtue.transport.channel.Channel;

public class ServerHandlerExceptionEvent extends ChannelHandlerExceptionEvent {

    public ServerHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(channel, cause);
    }

}
