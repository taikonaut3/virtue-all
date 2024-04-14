package io.virtue.transport.netty;

import io.netty.handler.timeout.IdleStateHandler;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * Base on {@link IdleStateHandler}.
 */
@Sharable
@Getter
@Accessors(fluent = true)
public class NettyIdeStateHandler extends IdleStateHandler {

    private final NettyHeartBeatHandler handler;

    protected NettyIdeStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds,
                                   NettyHeartBeatHandler handler) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.MILLISECONDS);
        this.handler = handler;
    }

    /**
     * Create a {@link IdleStateHandler} for server.
     *
     * @param url
     * @return
     */
    public static NettyIdeStateHandler createForServer(URL url) {
        return createForServer(getKeeAliveTimeout(url), new NettyHeartBeatHandler(url, true));
    }

    /**
     * Create a {@link IdleStateHandler} for client.
     *
     * @param url
     * @return
     */
    public static NettyIdeStateHandler createForClient(URL url) {
        return createForClient(getKeeAliveTimeout(url), new NettyHeartBeatHandler(url, false));
    }

    protected static NettyIdeStateHandler createForServer(int heartbeatInterval, NettyHeartBeatHandler handler) {
        return new NettyIdeStateHandler(heartbeatInterval, 0, heartbeatInterval, handler);
    }

    protected static NettyIdeStateHandler createForClient(int heartbeatInterval, NettyHeartBeatHandler handler) {
        return new NettyIdeStateHandler(0, heartbeatInterval, heartbeatInterval, handler);
    }

    private static int getKeeAliveTimeout(URL url) {
        return url.getIntParam(Key.KEEP_ALIVE_TIMEOUT, Constant.DEFAULT_KEEP_ALIVE_TIMEOUT);
    }

}
