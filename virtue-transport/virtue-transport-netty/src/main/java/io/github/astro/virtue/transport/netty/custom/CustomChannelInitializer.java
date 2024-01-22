package io.github.astro.virtue.transport.netty.custom;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.netty.NettyIdeStateHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/10 9:50
 */
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    private final Codec codec;

    public CustomChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
        this.url = url;
        this.handler = handler;
        this.codec = codec;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyCustomCodec nettyCustomCodec = new NettyCustomCodec(url, codec);
        int keepAliveTimeout = url.getIntParameter(Key.KEEP_ALIVE_TIMEOUT, Constant.DEFAULT_KEEP_ALIVE_TIMEOUT);
        IdleStateHandler idleStateHandler = NettyIdeStateHandler.createClientIdleStateHandler(keepAliveTimeout);
        socketChannel.pipeline()
                .addLast("decoder", nettyCustomCodec.getDecoder())
                .addLast("encoder", nettyCustomCodec.getEncoder())
                .addLast("heartbeat", idleStateHandler)
                .addLast("handler", handler);
    }
}
