package io.virtue.transport.netty.http.h2.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h2.NettyHttp2Headers;
import io.virtue.transport.netty.http.h2.NettyHttp2Stream;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.virtue.transport.util.TransportUtil.getScheme;

/**
 * Http2 client message converter.
 */
public class Http2ClientMessageConverter {

    @Sharable
    public static class RequestConverter extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Request request) {
                NettyHttpRequest httpRequest = (NettyHttpRequest) request.message();
                URL url = httpRequest.url();
                Http2Headers headers = ((NettyHttp2Headers) httpRequest.headers()).headers();
                headers.scheme(getScheme(url));
                headers.method(HttpMethod.getOf(url).name());
                headers.path(url.pathAndParams());
                ByteBuf data = Unpooled.wrappedBuffer(httpRequest.data());
                Http2StreamFrame[] frames = NettySupport.convertToHttp2StreamFrames(headers, data);
                NettySupport.bindUrlToChannel(request.url(), ctx.channel());
                for (Http2StreamFrame frame : frames) {
                    ctx.write(frame);
                }
                ctx.flush();
            } else {
                super.write(ctx, msg, promise);
            }
        }
    }

    @Sharable
    public static class ResponseConverter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof NettyHttp2Stream http2Stream) {
                URL url = NettySupport.getUrlFormChannel(ctx.channel());
                URL requestUrl = url.get(Key.HTTP_URL);
                HttpResponseStatus responseStatus = HttpResponseStatus.parseLine(http2Stream.http2Headers().status());
                NettyHttpResponse httpResponse = new NettyHttpResponse(
                        HttpVersion.HTTP_2_0,
                        requestUrl,
                        responseStatus,
                        http2Stream.headers(),
                        http2Stream.data());
                msg = responseStatus == OK
                        ? Response.success(url, httpResponse)
                        : Response.error(url, httpResponse);
                NettySupport.removeUrlFromChannel(ctx.channel());
            }
            super.channelRead(ctx, msg);
        }
    }

}
