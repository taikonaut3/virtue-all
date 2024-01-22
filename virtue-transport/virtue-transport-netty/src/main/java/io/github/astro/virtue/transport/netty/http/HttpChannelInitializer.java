package io.github.astro.virtue.transport.netty.http;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.netty.NettyIdeStateHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/10 9:39
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    private final boolean isServer;

    public HttpChannelInitializer(URL url, ChannelHandler handler, boolean isServer) {
        this.url = url;
        this.handler = handler;
        this.isServer = isServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        int keepAliveTimeout = url.getIntParameter(Key.KEEP_ALIVE_TIMEOUT, Constant.DEFAULT_KEEP_ALIVE_TIMEOUT);
        IdleStateHandler idleStateHandler = NettyIdeStateHandler.createServerIdleStateHandler(keepAliveTimeout);
        socketChannel.pipeline()
                .addLast("codec", isServer ? new HttpServerCodec() : new HttpClientCodec())
                .addLast("aggregator", new HttpObjectAggregator(65536))
                .addLast("heartbeat", idleStateHandler)
                .addLast("handler", handler);
    }
}
