package io.virtue.transport.netty.http2.server;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ResourceException;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdeStateHandler;
import io.virtue.transport.netty.http.server.HttpServerMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.transport.netty.http2.Util.getSslBytes;
import static io.virtue.transport.netty.http2.Util.readBytes;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerChannelInitializer.class);
    private static final byte[] CA_BYTES;
    private static final byte[] SERVER_CERT_BYTES;
    private static final byte[] SERVER_KEY_BYTES;
    private final URL url;
    private final ChannelHandler handler;
    private final NettyIdeStateHandler idleStateHandler;
    private final HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;

    static {
        try {
            CA_BYTES = getSslBytes(Key.CA_PATH, Constant.INTERNAL_CERTS_PATH + "ca-cert.pem");
            SERVER_CERT_BYTES = getSslBytes(Key.SERVER_CERT_PATH, Constant.INTERNAL_CERTS_PATH + "server-cert.pem");
            SERVER_KEY_BYTES = getSslBytes(Key.SERVER_KEY_PATH, Constant.INTERNAL_CERTS_PATH + "server-pkcs8-key.pem");
        } catch (Exception e) {
            throw new ResourceException("Get ssl config exception", e);
        }
    }

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
                .addLast(idleStateHandler)
                .addLast(idleStateHandler.handler())
                .addLast(sourceCodec)
                .addLast(new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory))
                .addLast(new Http2ToHttpHandler());
    }

    private SslContext getSslContext() throws Exception {
        boolean ssl = sslEnabled(url);
        SslContext sslContext = null;
        if (ssl) {
            SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            //SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(readBytes(SERVER_CERT_BYTES), readBytes(SERVER_KEY_BYTES))
                    .sslProvider(provider)
                    /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                     * Please refer to the HTTP/2 specification for cipher requirements. */
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .trustManager(readBytes(CA_BYTES))
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
