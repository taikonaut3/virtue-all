package io.virtue.transport.supprot;

import io.virtue.event.AbstractEvent;
import io.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * HeartBeatEvent.
 */
@Getter
@Accessors(fluent = true)
public class RefreshHeartBeatCountEvent extends AbstractEvent<Channel> {

    private final Integer readIdeTimes;

    private final Integer writeIdeTimes;

    private final Integer allIdeTimes;

    public RefreshHeartBeatCountEvent(Channel channel, Integer readIdeTimes, Integer writeIdeTimes, Integer allIdeTimes) {
        super(channel);
        this.readIdeTimes = readIdeTimes;
        this.writeIdeTimes = writeIdeTimes;
        this.allIdeTimes = allIdeTimes;
    }

    /**
     * Create for client.
     *
     * @param channel
     * @return
     */
    public static RefreshHeartBeatCountEvent buildForClient(Channel channel) {
        return new RefreshHeartBeatCountEvent(channel, null, 0, 0);
    }

    /**
     * Create for server.
     *
     * @param channel
     * @return
     */
    public static RefreshHeartBeatCountEvent buildForServer(Channel channel) {
        return new RefreshHeartBeatCountEvent(channel, 0, null, 0);
    }

}
