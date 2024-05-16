package io.virtue.transport.netty.http.h1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.client.NettyClient;
import io.virtue.transport.netty.client.NettyClientChannelHandler;

/**
 * Http Client.
 */
public class HttpClient extends NettyClient {

    private FixedChannelPool channelPool;

    public HttpClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
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
        channelPool = new FixedChannelPool(bootstrap, new HttpClientChannelInitializer(url, nettyHandler, this), 2);
    }

    @Override
    protected void doConnect() throws ConnectException {
        // here is no longer connecting and getting the channel
        // the connection is acquired from the channel pool when sending
    }

    @Override
    public void send(Object message) {
        Future<Channel> channelFuture = channelPool.acquire();
        channelFuture.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                Channel ch = future.getNow();
                ch.writeAndFlush(message);
            } else if (future.cause() != null) {
                throw new ConnectException(future.cause());
            } else {
                throw new ConnectException("Unknown Exception");
            }
        });
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
