package io.github.astro.virtue.rpc.event;

import io.github.astro.virtue.event.AbstractEvent;
import io.github.astro.virtue.transport.channel.Channel;

public class HeartBeatEvent extends AbstractEvent<Channel> {

    public HeartBeatEvent(Channel channel) {
        super(channel);
    }

}
