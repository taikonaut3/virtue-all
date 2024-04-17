package io.virtue.transport.supprot;

import io.virtue.event.AbstractEvent;
import io.virtue.transport.channel.Channel;

/**
 * HeartBeatEvent.
 */
public class IdleEvent extends AbstractEvent<Channel> {

    public IdleEvent(Channel channel) {
        super(channel);
    }

}
