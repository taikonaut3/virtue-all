package io.virtue.transport.netty.server;

import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.netty.NettyChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Base channel handler for netty server.
 */
@io.netty.channel.ChannelHandler.Sharable
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
        //NettyChannel.removeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        if (evt instanceof IdleStateEvent event) {
            if ((event.state() == IdleState.ALL_IDLE) || (event.state() == IdleState.READER_IDLE)) {
                channelHandler.heartBeat(nettyChannel, evt);
            }
        }
    }

}
