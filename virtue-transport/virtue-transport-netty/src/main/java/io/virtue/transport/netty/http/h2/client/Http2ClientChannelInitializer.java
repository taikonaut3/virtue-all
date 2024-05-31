package io.virtue.transport.netty.http.h2.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.ProtocolAdapter;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_2;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final SslContext sslContext;

    private final ChannelHandler handler;

    public Http2ClientChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForClient(HTTP_2) : null;
        this.handler = new Http2ClientHandler(url, handler);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        ProtocolAdapter.configClientChannelPipeline(url, socketChannel, sslContext, null, idleStateHandler, handler);
    }
}
