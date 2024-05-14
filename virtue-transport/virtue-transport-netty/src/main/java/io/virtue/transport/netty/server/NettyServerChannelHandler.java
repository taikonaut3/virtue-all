package io.virtue.transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.netty.NettyChannel;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * Base channelHandler for netty server.
 */
@Sharable
public final class NettyServerChannelHandler extends ChannelInboundHandlerAdapter {

    private final ChannelHandler channelHandler;

    public NettyServerChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.connected(nettyChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.disconnected(nettyChannel);
        NettyChannel.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.received(nettyChannel, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.caught(nettyChannel, cause);
    }

}
