package io.virtue.rpc.event;

import io.virtue.event.AbstractEvent;
import io.virtue.transport.channel.Channel;

public class HeartBeatEvent extends AbstractEvent<Channel> {

    public HeartBeatEvent(Channel channel) {
        super(channel);
    }

}
