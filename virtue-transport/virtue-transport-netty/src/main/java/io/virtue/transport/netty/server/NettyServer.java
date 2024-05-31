package io.virtue.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.BindException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.ProtocolAdapter;
import io.virtue.transport.server.AbstractServer;

/**
 * Base on netty server.
 */
public class NettyServer extends AbstractServer {

    protected Channel channel;

    private ServerBootstrap bootstrap;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    public NettyServer(URL url, ChannelHandler handler, Codec codec) throws BindException {
        super(url, handler, codec);
    }

    @Override
    protected void doInit() throws BindException {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-server-boss", true));
        workerGroup = new NioEventLoopGroup(Constant.DEFAULT_IO_THREADS, new DefaultThreadFactory("netty-server-worker", true));
        soBacklog = url.getIntParam(Key.SO_BACKLOG, Constant.DEFAULT_SO_BACKLOG);
        var handler = new NettyServerChannelHandler(channelHandler);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, soBacklog)
                //.option(ChannelOption.TCP_FASTOPEN_CONNECT, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(ProtocolAdapter.acquireServerChannelInitializer(url, handler, codec));
    }

    @Override
    protected void doBind() throws BindException {
        ChannelFuture future = bootstrap.bind(port());
        future.syncUninterruptibly();
        channel = future.channel();
    }

    @Override
    protected void doClose() throws NetWorkException {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
