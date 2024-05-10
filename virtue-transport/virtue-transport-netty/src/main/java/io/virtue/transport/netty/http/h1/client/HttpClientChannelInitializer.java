package io.virtue.transport.netty.http.h1.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;
    private final ChannelHandler handler;
    private final SslContext sslContext;

    public HttpClientChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForClient(HTTP_1_1) : null;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        int maxReceiveSize = url.getIntParam(Key.CLIENT_MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        HttpClientMessageConverter converter = new HttpClientMessageConverter();
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslContext != null) {
            pipeline.addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
        }

        pipeline.addLast("heartbeat", idleStateHandler)
                .addLast("heartbeatHandler", idleStateHandler.handler())
                .addLast("httpClientCodec", new HttpClientCodec())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast("responseConverter", converter.responseConverter())
                .addLast("requestConverter", converter.requestConverter())
                .addLast("handler", handler);
    }
}
