package io.virtue.transport.netty.http.h2.server;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.http.SslContextFactory;
import io.virtue.transport.netty.http.h1.server.HttpServerMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_1_1;
import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_2;
import static io.virtue.transport.util.TransportUtil.sslEnabled;

/**
 * Initializes the channel of Netty for HTTP2 Codec.
 */
public class Http2ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerChannelInitializer.class);
    private final URL url;
    private final ChannelHandler handler;
    private final NettyIdleStateHandler idleStateHandler;
    private final HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;
    private final SslContext sslContext;

    public Http2ServerChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        this.idleStateHandler = NettyIdleStateHandler.createForServer(url);
        this.sslContext = sslEnabled(url) ? SslContextFactory.createForServer(HTTP_2, HTTP_1_1) : null;
        this.upgradeCodecFactory = protocol -> {
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
        ch.pipeline()
                .addLast(sslCtx.newHandler(ch.alloc()))
                .addLast(new Http2OrHttpNegotiationHandler(url, idleStateHandler, handler));
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

    class Http2ToHttpHandler extends SimpleChannelInboundHandler<HttpMessage> {

        private final ChannelHandler requestHandler;
        private final ChannelHandler httpObjectAggregator;

        Http2ToHttpHandler() {
            int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
            httpObjectAggregator = new HttpObjectAggregator(maxReceiveSize);
            requestHandler = new HttpServerMessageConverter(url).requestConverter();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
            // If this handler is hit then no upgrade has been attempted and the client is just talking HTTP.
            logger.error("Directly talking: " + msg.protocolVersion() + " (no upgrade was attempted)");
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addAfter(ctx.name(), "requestConverter", requestHandler)
                    .addAfter("requestConverter", "handler", handler);
            pipeline.replace(this, null, httpObjectAggregator);
            ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
        }
    }
}
