package io.virtue.transport.netty.http.h2.client;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.http.SslContextFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_2;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;
    private final NettyIdleStateHandler idleStateHandler;
    private final SslContext sslContext;

    public Http2ClientChannelInitializer(URL url) {
        this.url = url;
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForClient(HTTP_2, HTTP_1_1) : null;
        this.idleStateHandler = NettyIdleStateHandler.createForClient(url);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }
        final Http2FrameCodec http2FrameCodec = Http2FrameCodecBuilder.forClient()
                .initialSettings(Http2Settings.defaultSettings()) // this is the default, but shows it can be changed.
                .build();
        pipeline.addLast(idleStateHandler)
                .addLast(idleStateHandler.handler())
                .addLast(http2FrameCodec)
                // this parameter ChannelInboundHandlerAdapter is Invalid for client
                .addLast(new Http2MultiplexHandler(new ChannelInboundHandlerAdapter()));
    }
}
