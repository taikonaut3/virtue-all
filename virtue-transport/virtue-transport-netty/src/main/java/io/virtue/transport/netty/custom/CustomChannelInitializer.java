package io.virtue.transport.netty.custom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyIdeStateHandler;

/**
 * Initializes the channel of Netty for Custom Codec.
 */
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    private final Codec codec;

    private final boolean isServer;

    public CustomChannelInitializer(URL url, ChannelHandler handler, Codec codec, boolean isServer) {
        this.url = url;
        this.handler = handler;
        this.codec = codec;
        this.isServer = isServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyCustomCodec nettyCustomCodec = new NettyCustomCodec(url, codec, isServer);
        int keepAliveTimeout = url.getIntParam(Key.KEEP_ALIVE_TIMEOUT, Constant.DEFAULT_KEEP_ALIVE_TIMEOUT);
        IdleStateHandler idleStateHandler = NettyIdeStateHandler.create(keepAliveTimeout, isServer);
        socketChannel.pipeline()
                .addLast("decoder", nettyCustomCodec.getDecoder())
                .addLast("encoder", nettyCustomCodec.getEncoder())
                .addLast("heartbeat", idleStateHandler)
                .addLast("handler", handler);
    }
}
