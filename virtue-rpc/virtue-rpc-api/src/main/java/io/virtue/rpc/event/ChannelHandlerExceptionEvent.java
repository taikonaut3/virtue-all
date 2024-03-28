package io.virtue.rpc.event;

import io.virtue.event.AbstractEvent;
import io.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Abstract ChannelHandlerExceptionEvent for server and client.
 */
@Getter
@Accessors(fluent = true)
public abstract class ChannelHandlerExceptionEvent extends AbstractEvent<Throwable> {

    private final Channel channel;

    public ChannelHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(cause);
        this.channel = channel;
    }

}
