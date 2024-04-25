package io.virtue.transport.util;

import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.event.Event;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.supprot.RefreshHeartBeatCountEvent;

import java.util.List;

import static io.virtue.common.constant.Components.Protocol.H2;
import static io.virtue.common.constant.Components.Protocol.H2C;

/**
 * Transport Util.
 */
public class TransportUtil {

    public static HttpMethod getHttpMethod(Invocation invocation) {
        return getHttpMethod(invocation.url());
    }

    public static HttpMethod getHttpMethod(URL url) {
        return url.get(HttpMethod.ATTRIBUTE_KEY);
    }

    public static String getScheme(URL url) {
        String protocol = url.protocol();
        if (protocol.equals(H2)) {
            return "https";
        } else if (protocol.equals(H2C)) {
            return "http";
        }
        throw new IllegalArgumentException("Unsupported protocol: " + protocol);
    }

    /**
     * Check whether it is SSL.
     *
     * @param url
     * @return
     */
    public static boolean sslEnabled(URL url) {
        List<String> sslProtocols = List.of(H2);
        return sslProtocols.contains(url.protocol());
    }



    /**
     * Push refresh heartbeat count event.
     *
     * @param channel
     * @param virtue
     * @param isServer
     */
    public static void publishRefreshHeartbeatCountEvent(Channel channel, Virtue virtue, boolean isServer) {
        Event<?> event = isServer
                ? RefreshHeartBeatCountEvent.buildForServer(channel)
                : RefreshHeartBeatCountEvent.buildForClient(channel);
        virtue.eventDispatcher().dispatch(event);
    }

}
