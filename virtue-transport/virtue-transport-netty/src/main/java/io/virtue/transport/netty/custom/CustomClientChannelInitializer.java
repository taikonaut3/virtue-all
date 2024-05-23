package io.virtue.transport.netty.custom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.ProtocolAdapter;

/**
 * Initializes the channel of Netty for Custom Codec.
 */
public class CustomClientChannelInitializer extends AbstractChannelPoolHandler {

    private final URL url;
    private final Codec codec;
    private final ChannelHandler handler;

    public CustomClientChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
        this.url = url;
        this.codec = codec;
        this.handler = handler;
    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        ProtocolAdapter.configClientChannelPipeline(url, channel, null, codec, idleStateHandler, handler);
    }
}
