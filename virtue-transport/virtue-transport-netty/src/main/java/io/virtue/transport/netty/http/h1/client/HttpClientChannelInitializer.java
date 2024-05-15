package io.virtue.transport.netty.http.h1.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * HttpClient ChannelInitializer.
 */
public class HttpClientChannelInitializer extends AbstractChannelPoolHandler {

    private final URL url;
    private final ChannelHandler[] handlers;
    private final SslContext sslContext;

    public HttpClientChannelInitializer(URL url, ChannelHandler handler, HttpClient httpClient) {
        this.url = url;
        this.handlers = NettySupport.createHttpClientHandlers(httpClient, handler);
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForClient(HTTP_1_1) : null;
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        int maxReceiveSize = url.getIntParam(Key.CLIENT_MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null) {
            pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
        }
        pipeline.addLast("heartbeat", idleStateHandler)
                .addLast("heartbeatHandler", idleStateHandler.handler())
                .addLast("httpClientCodec", new HttpClientCodec())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast(handlers);
    }

}
