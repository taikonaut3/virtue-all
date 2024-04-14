package io.virtue.transport.netty.custom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
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
        NettyIdeStateHandler idleStateHandler = isServer ? NettyIdeStateHandler.createForServer(url)
                : NettyIdeStateHandler.createForClient(url);
        socketChannel.pipeline()
                .addLast("decoder", nettyCustomCodec.getDecoder())
                .addLast("encoder", nettyCustomCodec.getEncoder())
                .addLast("idleState", idleStateHandler)
                .addLast("heartbeat", idleStateHandler.handler())
                .addLast("handler", handler);
    }
}
