package io.virtue.transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.virtue.transport.Response;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.netty.NettyChannel;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * Base channel handler for netty client.
 */
@Sharable
public final class NettyClientChannelHandler extends SimpleChannelInboundHandler<Response> {

    private final ChannelHandler channelHandler;

    public NettyClientChannelHandler(ChannelHandler channelHandler) {
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
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.received(nettyChannel, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        channelHandler.caught(nettyChannel, cause);
    }

}
