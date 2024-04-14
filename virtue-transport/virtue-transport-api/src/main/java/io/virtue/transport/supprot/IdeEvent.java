package io.virtue.transport.supprot;

import io.virtue.event.AbstractEvent;
import io.virtue.transport.channel.Channel;

/**
 * HeartBeatEvent.
 */
public class IdeEvent extends AbstractEvent<Channel> {

    public IdeEvent(Channel channel) {
        super(channel);
    }

}
