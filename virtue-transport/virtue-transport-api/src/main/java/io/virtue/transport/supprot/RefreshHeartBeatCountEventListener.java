package io.virtue.transport.supprot;

import io.virtue.common.constant.Key;
import io.virtue.event.EventListener;
import io.virtue.transport.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The count is refreshed when the current channel information is transmitted.
 */
public class RefreshHeartBeatCountEventListener implements EventListener<RefreshHeartBeatCountEvent> {

    @Override
    public void onEvent(RefreshHeartBeatCountEvent event) {
        Channel channel = event.source();
        Integer writeIdeTimes = event.writeIdeTimes();
        Integer readIdeTimes = event.readIdeTimes();
        Integer allIdeTimes = event.allIdeTimes();
        if (writeIdeTimes != null) {
            channel.set(Key.WRITE_IDLE_TIMES, new AtomicInteger(writeIdeTimes));
        }
        if (readIdeTimes != null) {
            channel.set(Key.READER_IDLE_TIMES, new AtomicInteger(readIdeTimes));
        }
        if (allIdeTimes != null) {
            channel.set(Key.ALL_IDLE_TIMES, new AtomicInteger(allIdeTimes));
        }
    }

}
