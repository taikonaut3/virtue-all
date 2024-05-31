package io.virtue.transport.netty.http.h2.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.ProtocolAdapter;
import io.virtue.transport.netty.client.NettyPoolClient;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_2;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * HttpClient ChannelInitializer.
 */
public class Http2ClientPoolChannelInitializer extends AbstractChannelPoolHandler {

    private final URL url;

    private final SslContext sslContext;

    private final ChannelHandler handler;

    public Http2ClientPoolChannelInitializer(URL url, ChannelHandler handler, NettyPoolClient client) {
        this.url = url;
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForClient(HTTP_2) : null;
        this.handler = new Http2ClientHandler(url, handler);
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        ProtocolAdapter.configClientChannelPipeline(url, ch, sslContext, null, idleStateHandler, handler);
    }

}
