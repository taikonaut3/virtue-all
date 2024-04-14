package io.virtue.transport.netty.http2.client;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdeStateHandler;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    public Http2ClientChannelInitializer(URL url) {
        this.url = url;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        SslContext sslContext = getSslContext();
        ChannelPipeline pipeline = socketChannel.pipeline();
        NettyIdeStateHandler idleStateHandler = NettyIdeStateHandler.createForClient(url);
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }
        final Http2FrameCodec http2FrameCodec = Http2FrameCodecBuilder.forClient()
                .initialSettings(Http2Settings.defaultSettings()) // this is the default, but shows it can be changed.
                .build();
        pipeline.addLast(http2FrameCodec)
                .addLast(idleStateHandler)
                .addLast(idleStateHandler.handler())
                // this parameter ChannelInboundHandlerAdapter is Invalid for client
                .addLast(new Http2MultiplexHandler(new ChannelInboundHandlerAdapter()));
    }

    private SslContext getSslContext() throws Exception {
        boolean ssl = url.getBooleanParam(Key.SSL, true);
        SslContext sslContext = null;
        if (ssl) {
            final SslProvider provider =
                    SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            sslContext = SslContextBuilder.forClient()
                    .sslProvider(provider)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    // you probably won't want to use this in production, but it is fine for this example:
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2,
                            ApplicationProtocolNames.HTTP_1_1))
                    .build();
        }
        return sslContext;
    }
}
