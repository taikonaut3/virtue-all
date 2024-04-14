package io.virtue.transport.netty.http2.server;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdeStateHandler;
import io.virtue.transport.netty.http.server.HttpServerMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerChannelInitializer.class);
    private final URL url;
    private final ChannelHandler handler;
    private final NettyIdeStateHandler idleStateHandler;
    private final HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;

    public Http2ServerChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        idleStateHandler = NettyIdeStateHandler.createForServer(url);
        upgradeCodecFactory = protocol -> {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(
                        Http2FrameCodecBuilder.forServer().build(),
                        new Http2MultiplexHandler(new Http2ServerHandler(url, handler))
                );
            } else {
                return null;
            }
        };
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        SslContext sslContext = getSslContext();
        if (sslContext != null) {
            configureSsl(sslContext, socketChannel);
        } else {
            configureClearText(socketChannel);
        }

    }

    /**
     * Configure the pipeline for TLS NPN negotiation to HTTP/2.
     *
     * @param sslCtx
     * @param ch
     */
    private void configureSsl(SslContext sslCtx, SocketChannel ch) {
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new Http2OrHttpNegotiationHandler(url, idleStateHandler, handler));
    }

    /**
     * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.0.
     *
     * @param ch
     */
    private void configureClearText(SocketChannel ch) {
        final HttpServerCodec sourceCodec = new HttpServerCodec();
        ch.pipeline()
                .addLast(sourceCodec)
                .addLast(idleStateHandler)
                .addLast(idleStateHandler.handler())
                .addLast(new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory))
                .addLast(new Http2ToHttpHandler());
    }

    private SslContext getSslContext() throws Exception {
        boolean ssl = url.getBooleanParam(Key.SSL, true);
        SslContext sslContext = null;
        if (ssl) {
            SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .sslProvider(provider)
                    /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                     * Please refer to the HTTP/2 specification for cipher requirements. */
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .applicationProtocolConfig(
                            new ApplicationProtocolConfig(
                                    ApplicationProtocolConfig.Protocol.ALPN,
                                    // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                                    ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                    // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                    ApplicationProtocolNames.HTTP_2,
                                    ApplicationProtocolNames.HTTP_1_1)
                    ).build();
        }
        return sslContext;
    }

    class Http2ToHttpHandler extends SimpleChannelInboundHandler<HttpMessage> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
            // If this handler is hit then no upgrade has been attempted and the client is just talking HTTP.
            logger.error("Directly talking: " + msg.protocolVersion() + " (no upgrade was attempted)");
            ChannelPipeline pipeline = ctx.pipeline();
            int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
            pipeline.addAfter(ctx.name(), null, new HttpServerMessageConverter().requestConverter());
            pipeline.replace(this, null, new HttpObjectAggregator(maxReceiveSize));
            ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
        }
    }
}
