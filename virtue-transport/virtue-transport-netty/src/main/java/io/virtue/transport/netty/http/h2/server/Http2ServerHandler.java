package io.virtue.transport.netty.http.h2.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.transport.Request;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.VirtueHttpHeaderNames;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.h2.NettyHttp2Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.virtue.transport.netty.http.h2.Util.currentStream;
import static io.virtue.transport.netty.http.h2.Util.removeCurrentStream;

/**
 * Http2 Server Handler.
 */
@Sharable
public final class Http2ServerHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerHandler.class);
    private final URL url;
    private final ChannelHandler handler;

    Http2ServerHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addLast(handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
        logger.error("Http2ServerHandler exception", cause);
        ctx.close();
    }

    /**
     * handle headers frame.
     *
     * @param ctx
     * @param headersFrame
     * @throws Exception
     */
    private void onHeadersRead(ChannelHandlerContext ctx, Http2HeadersFrame headersFrame) throws Exception {
        var currentStream = currentStream(ctx, headersFrame.stream().id(), url);
        currentStream.parseHeaderFrame(headersFrame);
        if (currentStream.endStream()) {
            removeCurrentStream(ctx, currentStream);
            fireChannelRead(ctx, currentStream);
        }
    }

    /**
     * Handle data frame.
     *
     * @param ctx
     * @param dataFrame
     * @throws Exception
     */
    private void onDataRead(ChannelHandlerContext ctx, Http2DataFrame dataFrame) throws Exception {
        var currentStream = currentStream(ctx, dataFrame.stream().id(), url);
        // todo There appears to be a copy of the data twice?
        currentStream.parseDataFrame(dataFrame);
        if (currentStream.endStream()) {
            removeCurrentStream(ctx, currentStream);
            fireChannelRead(ctx, currentStream);
        }
        dataFrame.release();
    }

    private void fireChannelRead(ChannelHandlerContext ctx, NettyHttp2Stream stream) {
        NettyChannel nettyChannel = NettyChannel.getChannel(ctx.channel());
        nettyChannel.set(URL.ATTRIBUTE_KEY, this.url);
        URL url = stream.url();
        NettyHttpRequest httpRequest = new NettyHttpRequest(HttpVersion.HTTP_2_0, stream.url(), stream.headers(), stream.data());
        CharSequence virtueUrlStr = httpRequest.headers().get(VirtueHttpHeaderNames.VIRTUE_URL.getName());
        if (!StringUtil.isBlank(virtueUrlStr)) {
            url = URL.valueOf(virtueUrlStr.toString());
        }
        Request request = new Request(url, httpRequest);
        ctx.fireChannelRead(request);
    }
}
