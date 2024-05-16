package io.virtue.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.ProtocolAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Netty Pool Client.
 */
public class NettyPoolClient extends NettyClient {

    protected FixedChannelPool channelPool;

    public NettyPoolClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);
    }

    @Override
    protected void doInit() throws ConnectException {
        nettyHandler = new NettyClientChannelHandler(super.channelHandler);
        bootstrap = new Bootstrap();
        bootstrap.group(NIO_EVENT_LOOP_GROUP)
                .channel(NioSocketChannel.class)
                .remoteAddress(inetSocketAddress())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        ChannelPoolHandler channelPoolHandler = ProtocolAdapter.acquireClientChannelPoolHandler(url, nettyHandler, this);
        int maxConnections = url.getIntParam(Key.MAX_CONNECTIONS, Constant.DEFAULT_CLIENT_MAX_CONNECTIONS);
        channelPool = new FixedChannelPool(bootstrap, channelPoolHandler, maxConnections);
    }

    @Override
    protected void doConnect() throws ConnectException {
        // here is no longer connecting and getting the channel
        // the connection is acquired from the channel pool when sending
    }

    @Override
    public void send(Object message) {
        Future<Channel> future = channelPool.acquire();
        boolean success = future.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
        if (success && future.isSuccess()) {
            Channel ch = future.getNow();
            doSend(ch, message);
        } else if (future.cause() != null) {
            throw new ConnectException(future.cause());
        } else {
            throw new ConnectException("Unknown Exception");
        }
    }

    protected void doSend(Channel channel, Object message) {
        channel.writeAndFlush(message);
        release(channel);
    }

    @Override
    public boolean isActive() {
        return isInit;
    }

    @Override
    public void close() throws NetWorkException {
        channelPool.close();
    }

    /**
     * Release the channel.
     *
     * @param channel
     */
    public void release(Channel channel) {
        channelPool.release(channel);
    }
}
