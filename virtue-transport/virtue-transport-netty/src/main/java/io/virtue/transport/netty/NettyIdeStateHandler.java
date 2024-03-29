package io.virtue.transport.netty;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Base on {@link IdleStateHandler}.
 */
public class NettyIdeStateHandler extends IdleStateHandler {

    protected NettyIdeStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.MILLISECONDS);
    }

    protected static IdleStateHandler createForServer(int heartbeatInterval) {
        return new NettyIdeStateHandler(heartbeatInterval, 0, heartbeatInterval);
    }

    /**
     * Create a {@link IdleStateHandler} for client.
     *
     * @param heartbeatInterval
     * @return
     */
    protected static IdleStateHandler createForClient(int heartbeatInterval) {
        return new NettyIdeStateHandler(0, heartbeatInterval, heartbeatInterval);
    }

    /**
     * Create a {@link IdleStateHandler} for client or server.
     *
     * @param heartbeatInterval
     * @param isServer
     * @return
     */
    public static IdleStateHandler create(int heartbeatInterval, boolean isServer) {
        return isServer ? createForServer(heartbeatInterval) : createForClient(heartbeatInterval);
    }

}
