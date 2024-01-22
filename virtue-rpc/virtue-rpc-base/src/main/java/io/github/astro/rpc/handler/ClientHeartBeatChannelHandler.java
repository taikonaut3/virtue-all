package io.github.astro.rpc.handler;

import io.github.astro.rpc.event.HeartBeatEvent;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.base.HeartBeatChannelHandler;
import io.github.astro.virtue.transport.channel.Channel;

public class ClientHeartBeatChannelHandler extends HeartBeatChannelHandler {

    public ClientHeartBeatChannelHandler() {
        addWatchEventKey(Key.ALL_IDLE_TIMES);
        addWatchEventKey(Key.WRITE_IDLE_TIMES);
    }

    @Override
    public void heartBeat(Channel channel, Object event) {
        URL url = (URL) channel.getAttribute(Key.URL);
        if (url != null) {
            getEventDispatcher().dispatchEvent(new HeartBeatEvent(channel));
        }

    }

}
