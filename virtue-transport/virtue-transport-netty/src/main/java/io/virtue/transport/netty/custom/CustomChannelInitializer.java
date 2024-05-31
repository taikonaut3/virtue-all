package io.virtue.transport.netty.custom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.ProtocolAdapter;

/**
 * Initializes the channel of Netty for Custom Codec.
 */
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final Codec codec;

    private final ChannelHandler handler;

    private final boolean isServer;

    public CustomChannelInitializer(URL url, ChannelHandler handler, Codec codec, boolean isServer) {
        this.url = url;
        this.codec = codec;
        this.handler = handler;
        this.isServer = isServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        if (isServer) {
            ProtocolAdapter.configServerChannelPipeline(url, socketChannel, null, codec, idleStateHandler, handler);
        } else {
            ProtocolAdapter.configClientChannelPipeline(url, socketChannel, null, codec, idleStateHandler, handler);
        }
    }
}
