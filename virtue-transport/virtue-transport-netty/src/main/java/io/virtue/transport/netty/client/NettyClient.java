package io.virtue.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.virtue.common.constant.Constant;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.AbstractClient;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.ProtocolAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Client base on netty.
 */
public class NettyClient extends AbstractClient {

    protected static final NioEventLoopGroup NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup(
            Constant.DEFAULT_IO_THREADS, new DefaultThreadFactory("netty-client-worker", true)
    );

    protected Bootstrap bootstrap;
    protected Channel channel;
    protected io.netty.channel.ChannelHandler nettyHandler;

    public NettyClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);
    }

    /**
     * close NioEventLoopGroup.
     */
    public static void closeNioEventLoopGroup() {
        NIO_EVENT_LOOP_GROUP.shutdownGracefully();
    }

    @Override
    protected void doInit() throws ConnectException {
        bootstrap = new Bootstrap();
        nettyHandler = new NettyClientChannelHandler(super.channelHandler);
        bootstrap.group(NIO_EVENT_LOOP_GROUP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(ProtocolAdapter.acquireClientChannelInitializer(url, nettyHandler, codec));
    }

    @Override
    protected void doConnect() throws ConnectException {
        ChannelFuture future = bootstrap.connect(inetSocketAddress());
        boolean success = future.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
        if (success && future.isSuccess()) {
            channel = future.channel();
            super.channel = NettyChannel.getChannel(channel);
        } else if (future.cause() != null) {
            throw new ConnectException(future.cause());
        } else {
            throw new ConnectException("Unknown Exception");
        }
    }

    @Override
    public boolean isActive() {
        return channel != null && channel.isActive();
    }

}
