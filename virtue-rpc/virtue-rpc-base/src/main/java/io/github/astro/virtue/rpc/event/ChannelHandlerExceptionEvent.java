package io.github.astro.virtue.rpc.event;

import io.github.astro.virtue.event.AbstractEvent;
import io.github.astro.virtue.transport.channel.Channel;

public abstract class ChannelHandlerExceptionEvent extends AbstractEvent<Throwable> {

    private final Channel channel;

    public ChannelHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(cause);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

}
