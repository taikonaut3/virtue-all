package io.virtue.transport.netty.http.h1.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.ProtocolAdapter;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final SslContext sslContext;

    private final ChannelHandler[] handlers;

    public HttpServerChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForServer(HTTP_1_1) : null;
        this.handlers = NettySupport.createHttpServerHandlers(url, handler);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForServer(url);
        ProtocolAdapter.configServerChannelPipeline(url, socketChannel, sslContext, null, idleStateHandler, handlers);
    }
}
