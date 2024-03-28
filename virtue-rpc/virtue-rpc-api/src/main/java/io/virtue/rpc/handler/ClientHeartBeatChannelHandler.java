package io.virtue.rpc.handler;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.rpc.event.HeartBeatEvent;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.HeartBeatChannelHandler;

/**
 * Client HeartBeatChannelHandler.
 */
public class ClientHeartBeatChannelHandler extends HeartBeatChannelHandler {

    public ClientHeartBeatChannelHandler() {
        addWatchEventKey(Key.ALL_IDLE_TIMES_ATTRIBUTE_KEY);
        addWatchEventKey(Key.WRITE_IDLE_TIMES_ATTRIBUTE_KEY);
    }

    @Override
    public void heartBeat(Channel channel, Object event) {
        URL url = channel.attribute(URL.ATTRIBUTE_KEY).get();
        if (url != null) {
            getEventDispatcher(url).dispatchEvent(new HeartBeatEvent(channel));
        }

    }

}
