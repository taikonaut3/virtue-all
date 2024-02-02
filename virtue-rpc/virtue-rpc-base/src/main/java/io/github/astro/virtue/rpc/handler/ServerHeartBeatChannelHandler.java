package io.github.astro.virtue.rpc.handler;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.rpc.event.HeartBeatEvent;
import io.github.astro.virtue.transport.base.HeartBeatChannelHandler;
import io.github.astro.virtue.transport.channel.Channel;

public class ServerHeartBeatChannelHandler extends HeartBeatChannelHandler {

    public ServerHeartBeatChannelHandler() {
        addWatchEventKey(Key.ALL_IDLE_TIMES_ATTRIBUTE_KEY);
        addWatchEventKey(Key.READER_IDLE_TIMES_ATTRIBUTE_KEY);
    }

    @Override
    public void heartBeat(Channel channel, Object event) {
        URL url = channel.attribute(URL.ATTRIBUTE_KEY).get();
        if (url != null) {
            getEventDispatcher(url).dispatchEvent(new HeartBeatEvent(channel));
        }
    }

}
