package io.virtue.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.transport.supprot.IdleEvent;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.virtue.transport.util.TransportUtil.publishRefreshHeartbeatCountEvent;

/**
 * Netty HeartBeatHandler Adapter.
 */
@Sharable
public class NettyHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final URL url;

    private final Virtue virtue;

    private final Boolean isServer;

    public NettyHeartBeatHandler(URL url, boolean isServer) {
        this.url = url;
        this.isServer = isServer;
        this.virtue = isServer ? Virtue.ofServer(url) : Virtue.ofClient(url);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        nettyChannel.set(URL.ATTRIBUTE_KEY, url);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        publishRefreshHeartbeatCountEvent(nettyChannel, virtue, isServer);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        nettyChannel.remove(URL.ATTRIBUTE_KEY);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        if (evt instanceof IdleStateEvent event) {
            if (isServer) {
                if ((event.state() == IdleState.ALL_IDLE) || (event.state() == IdleState.READER_IDLE)) {
                    virtue.eventDispatcher().dispatch(new IdleEvent(nettyChannel));
                }
            } else {
                if ((event.state() == IdleState.ALL_IDLE) || (event.state() == IdleState.WRITER_IDLE)) {
                    virtue.eventDispatcher().dispatch(new IdleEvent(nettyChannel));
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
