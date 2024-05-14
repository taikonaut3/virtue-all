package io.virtue.transport.netty.http.h1.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
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
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;
    private final ChannelHandler[] handlers;
    private final SslContext sslContext;

    public HttpServerChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handlers = NettySupport.createHttpServerHandlers(url, handler);
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForServer(HTTP_1_1) : null;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForServer(url);
        int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);

        if (sslContext != null) {
            socketChannel.pipeline().addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
        }
        socketChannel.pipeline()
                .addLast("heartbeat", idleStateHandler)
                .addLast("heartbeatHandler", idleStateHandler.handler())
                .addLast("httpServerCodec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast(handlers);
    }
}
