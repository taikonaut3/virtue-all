package io.virtue.transport.netty.http.h1.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.netty.ByteBufUtil;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;
import lombok.Getter;
import lombok.experimental.Accessors;

import static io.netty.handler.codec.http.DefaultHttpHeadersFactory.trailersFactory;

/**
 * EnvelopeConverter.
 */
@Getter
@Accessors(fluent = true)
public final class HttpClientMessageConverter {

    private static final AttributeKey<URL> urlKey = AttributeKey.newInstance(Key.URL);
    private final ChannelHandler requestConverter = new RequestConverter();
    private final ChannelHandler responseConverter = new ResponseConverter();

    static class RequestConverter extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Request request) {
                HttpRequest httpRequest = (HttpRequest) request.message();
                DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1,
                        HttpMethod.valueOf(httpRequest.method().name()),
                        httpRequest.url().pathAndParams(),
                        Unpooled.wrappedBuffer(httpRequest.data()),
                        ((NettyHttpHeaders) httpRequest.headers()).httpHeaders(),
                        trailersFactory().newHeaders()
                );
                ctx.channel().attr(urlKey).set(request.url());
                ctx.write(fullHttpRequest, promise);
            }
        }
    }

    static class ResponseConverter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpResponse fullHttpResponse) {
                URL url = ctx.channel().attr(urlKey).get();
                ByteBuf byteBuf = fullHttpResponse.content();
                byte[] data = ByteBufUtil.getBytes(byteBuf);
                NettyHttpResponse httpResponse = new NettyHttpResponse(
                        io.virtue.transport.http.HttpVersion.HTTP_1_1,
                        url,
                        fullHttpResponse.status(),
                        new NettyHttpHeaders(fullHttpResponse.headers()),
                        data);
                msg = httpResponse.statusCode() == 200
                        ? Response.success(httpResponse.url(), httpResponse)
                        : Response.error(httpResponse.url(), httpResponse);
                fullHttpResponse.release();
            }
            ctx.fireChannelRead(msg);
        }
    }

}
