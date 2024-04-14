package io.virtue.transport.util;

import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.event.Event;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.supprot.RefreshHeartBeatCountEvent;

/**
 * Transport Util.
 */
public final class TransportUtil {

    public static HttpMethod getHttpMethod(Invocation invocation) {
        return getHttpMethod(invocation.url());
    }

    public static HttpMethod getHttpMethod(URL url) {
        return url.get(HttpMethod.ATTRIBUTE_KEY);
    }

    /**
     * Push refresh heartbeat count event.
     *
     * @param channel
     * @param virtue
     * @param isServer
     */
    public static void publishRefreshHeartbeatCountEvent(Channel channel, Virtue virtue, boolean isServer) {
        Event<?> event = isServer ? RefreshHeartBeatCountEvent.buildForServer(channel)
                : RefreshHeartBeatCountEvent.buildForClient(channel);
        virtue.eventDispatcher().dispatchEvent(event);
    }
}
