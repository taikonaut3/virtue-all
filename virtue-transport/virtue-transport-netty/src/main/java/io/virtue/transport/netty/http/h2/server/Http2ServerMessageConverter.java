package io.virtue.transport.netty.http.h2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.VirtueHttpHeaderNames;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h2.NettyHttp2Headers;
import io.virtue.transport.netty.http.h2.NettyHttp2Stream;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * Http2 server message converter.
 */
public class Http2ServerMessageConverter {

    @Sharable
    public static class RequestConverter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof NettyHttp2Stream http2Stream) {
                URL url = http2Stream.url();
                NettyHttpRequest httpRequest = new NettyHttpRequest(
                        HttpVersion.HTTP_2_0,
                        url,
                        http2Stream.headers(),
                        http2Stream.data());
                CharSequence virtueUrlStr = httpRequest.headers().get(VirtueHttpHeaderNames.VIRTUE_URL);
                if (!StringUtil.isBlank(virtueUrlStr)) {
                    url = URL.valueOf(virtueUrlStr.toString());
                }
                Request request = new Request(url, httpRequest);
                super.channelRead(ctx, request);
            }
        }
    }

    @Sharable
    public static class ResponseConverter extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Response response) {
                NettyHttpResponse httpResponse = (NettyHttpResponse) response.message();
                Http2Headers headers = ((NettyHttp2Headers) httpResponse.headers()).headers();
                ByteBuf data = Unpooled.wrappedBuffer(httpResponse.data());
                headers.status(HttpResponseStatus.valueOf(httpResponse.statusCode()).codeAsText());
                Http2StreamFrame[] frames = NettySupport.convertToHttp2StreamFrames(headers, data);
                for (Http2StreamFrame frame : frames) {
                    ctx.write(frame);
                }
                ctx.flush();
            }
        }
    }
}
