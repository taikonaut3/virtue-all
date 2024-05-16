package io.virtue.transport.netty.http.h1.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.client.NettyPoolClient;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.DefaultHttpHeadersFactory.trailersFactory;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Http client message converter.
 */
public final class HttpClientMessageConverter {

    /**
     * Request convert to FullHttpRequest.
     */
    @Sharable
    public static class RequestConverter extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Request request) {
                HttpRequest httpRequest = (HttpRequest) request.message();
                msg = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1,
                        HttpMethod.valueOf(httpRequest.method().name()),
                        httpRequest.url().pathAndParams(),
                        Unpooled.wrappedBuffer(httpRequest.data()),
                        ((NettyHttpHeaders) httpRequest.headers()).httpHeaders(),
                        trailersFactory().newHeaders()
                );
                NettySupport.bindUrlToChannel(request.url(), ctx.channel());
            }
            super.write(ctx, msg, promise);
        }
    }

    /**
     * FullHttpResponse convert to response.
     */
    @Sharable
    public static class ResponseConverter extends ChannelInboundHandlerAdapter {

        private final NettyPoolClient client;

        public ResponseConverter(NettyPoolClient client) {
            this.client = client;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpResponse fullHttpResponse) {
                URL url = NettySupport.getUrlFormChannel(ctx.channel());
                URL requestUrl = url.get(Key.HTTP_URL);
                HttpResponseStatus responseStatus = fullHttpResponse.status();
                NettyHttpResponse httpResponse = new NettyHttpResponse(
                        io.virtue.transport.http.HttpVersion.HTTP_1_1,
                        requestUrl,
                        responseStatus,
                        new NettyHttpHeaders(fullHttpResponse.headers()),
                        NettySupport.getBytes(fullHttpResponse.content()));
                msg = responseStatus == OK
                        ? Response.success(url, httpResponse)
                        : Response.error(url, httpResponse);
                NettySupport.removeUrlFromChannel(ctx.channel());
                fullHttpResponse.release();
                client.release(ctx.channel());
            }
            super.channelRead(ctx, msg);
        }
    }

}
