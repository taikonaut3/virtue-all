package io.virtue.transport.netty.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.transport.Request;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Headers;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Request;
import io.virtue.transport.netty.http2.envelope.StreamEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.virtue.transport.netty.http2.Util.getStreamEnvelope;
import static io.virtue.transport.netty.http2.Util.removeStreamEnvelope;

/**
 * Http5 Server Handler.
 */
@Sharable
public final class Http2ServerHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerHandler.class);
    private final URL url;

    private final Virtue virtue;
    private final ChannelHandler handler;

    Http2ServerHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.virtue = Virtue.ofServer(url);
        this.handler = handler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addLast(handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Http2StreamChannel streamChannel = (Http2StreamChannel) ctx.channel();
        streamChannel.parent();
        if (msg instanceof Http2HeadersFrame) {
            onHeadersRead(ctx, (Http2HeadersFrame) msg);
        } else if (msg instanceof Http2DataFrame) {
            onDataRead(ctx, (Http2DataFrame) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("Http2ServerHandler exceptionCaught", cause);
        ctx.close();
    }

    /**
     * Handle data frame.
     *
     * @param ctx
     * @param data
     * @throws Exception
     */
    private void onDataRead(ChannelHandlerContext ctx, Http2DataFrame data) throws Exception {
        StreamEnvelope streamEnvelope = getStreamEnvelope(ctx, data.stream().id(), url, true);
        // todo There appears to be a copy of the data twice?
        ByteBuf byteBuf = data.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        streamEnvelope.writeData(bytes);
        if (data.isEndStream()) {
            streamEnvelope.end();
            removeStreamEnvelope(ctx, data.stream().id());
            fireChannelRead(ctx, streamEnvelope);
        } else {
            // We do not send back the response to the remote-peer, so we need to release it.
            data.release();
        }
    }

    /**
     * handle headers frame.
     *
     * @param ctx
     * @param headers
     * @throws Exception
     */
    private void onHeadersRead(ChannelHandlerContext ctx, Http2HeadersFrame headers) throws Exception {
        StreamEnvelope streamEnvelope = getStreamEnvelope(ctx, headers.stream().id(), url, true);
        streamEnvelope.addHeaders(new NettyHttp2Headers(headers.headers()));
        if (headers.isEndStream()) {
            streamEnvelope.end();
            removeStreamEnvelope(ctx, headers.stream().id());
            fireChannelRead(ctx, streamEnvelope);
        }
    }

    private void fireChannelRead(ChannelHandlerContext ctx, StreamEnvelope message) {
        URL url = message.url();
        NettyHttp2Request http2Request = new NettyHttp2Request(message);
        Request request = new Request(url, http2Request);
        ctx.fireChannelRead(request);
    }
}
