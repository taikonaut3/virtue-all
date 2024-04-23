package io.virtue.transport.netty.http2.client;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ResourceException;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdeStateHandler;

import static io.virtue.transport.netty.http2.Util.getSslBytes;
import static io.virtue.transport.netty.http2.Util.readBytes;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final byte[] CA_BYTES;
    private static final byte[] CLIENT_CERT_BYTES;
    private static final byte[] CLIENT_KEY_BYTES;
    private final URL url;

    static {
        try {
            CA_BYTES = getSslBytes(Key.CA_PATH, Constant.INTERNAL_CERTS_PATH + "ca-cert.pem");
            CLIENT_CERT_BYTES = getSslBytes(Key.CLIENT_CERT_PATH, Constant.INTERNAL_CERTS_PATH + "client-cert.pem");
            CLIENT_KEY_BYTES = getSslBytes(Key.CLIENT_KEY_PATH, Constant.INTERNAL_CERTS_PATH + "client-pkcs8-key.pem");
        } catch (Exception e) {
            throw new ResourceException("Get ssl config exception", e);
        }
    }

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
        pipeline.addLast(idleStateHandler)
                .addLast(idleStateHandler.handler())
                .addLast(http2FrameCodec)
                // this parameter ChannelInboundHandlerAdapter is Invalid for client
                .addLast(new Http2MultiplexHandler(new ChannelInboundHandlerAdapter()));
    }

    private SslContext getSslContext() throws Exception {
        boolean ssl = sslEnabled(url);
        SslContext sslContext = null;
        if (ssl) {
            final SslProvider provider =
                    SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            sslContext = SslContextBuilder.forClient()
                    .sslProvider(provider)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .keyManager(readBytes(CLIENT_CERT_BYTES), readBytes(CLIENT_KEY_BYTES))
                    // you probably won't want to use this in production, but it is fine for this example:
                    .trustManager(readBytes(CA_BYTES))
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
