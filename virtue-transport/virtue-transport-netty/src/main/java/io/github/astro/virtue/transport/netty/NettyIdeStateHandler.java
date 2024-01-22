package io.github.astro.virtue.transport.netty;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyIdeStateHandler extends IdleStateHandler {

    protected NettyIdeStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.MILLISECONDS);
    }

    public static IdleStateHandler createServerIdleStateHandler(int heartbeatInterval) {
        return new NettyIdeStateHandler(heartbeatInterval, 0, heartbeatInterval);
    }

    public static IdleStateHandler createClientIdleStateHandler(int heartbeatInterval) {
        return new NettyIdeStateHandler(0, heartbeatInterval, heartbeatInterval);
    }

}
