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
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.AbstractClient;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.ProtocolInitializer;

import java.util.concurrent.TimeUnit;

/**
 * Base on netty client.
 */
public final class NettyClient extends AbstractClient {

    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Constant.DEFAULT_IO_THREADS,
            new DefaultThreadFactory("NettyClientWorker", true));

    private Bootstrap bootstrap;

    private Channel channel;

    public NettyClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);
    }

    @Override
    protected void doInit() throws ConnectException {
        bootstrap = new Bootstrap();
        int configTimeout = url.getIntParam(Key.CONNECT_TIMEOUT, Constant.DEFAULT_CONNECT_TIMEOUT);
        connectTimeout = Math.min(configTimeout, Constant.DEFAULT_MAX_CONNECT_TIMEOUT);
        final NettyClientChannelHandler handler = new NettyClientChannelHandler(channelHandler);
        initBootStrap(handler);
    }

    private void initBootStrap(NettyClientChannelHandler handler) {
        bootstrap.group(nioEventLoopGroup)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(ProtocolInitializer.getInitializer(url, handler, codec, false));
    }

    @Override
    protected void doConnect() throws ConnectException {
        ChannelFuture future = bootstrap.connect(toInetSocketAddress());
        boolean ret = future.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
        if (ret && future.isSuccess()) {
            channel = future.channel();
            super.channel = NettyChannel.getChannel(channel);
        } else if (future.cause() != null) {
            throw new ConnectException(future.cause());
        } else {
            throw new ConnectException("Unknown Exception");
        }
    }

    @Override
    protected void doClose() throws NetWorkException {
        try {
            nioEventLoopGroup.shutdownGracefully();
            NettyChannel.removeChannel(channel);
        } catch (Throwable e) {
            throw new NetWorkException(e);
        }
    }

    @Override
    public boolean isActive() {
        return channel != null && channel.isActive();
    }

}
