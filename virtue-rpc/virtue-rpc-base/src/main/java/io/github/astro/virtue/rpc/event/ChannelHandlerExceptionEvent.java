package io.github.astro.virtue.rpc.event;

import io.github.astro.virtue.event.AbstractEvent;
import io.github.astro.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public abstract class ChannelHandlerExceptionEvent extends AbstractEvent<Throwable> {

    private final Channel channel;

    public ChannelHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(cause);
        this.channel = channel;
    }

}
