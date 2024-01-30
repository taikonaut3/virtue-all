package io.github.astro.virtue.transport.netty.http;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * EnvelopeConverter
 */
@Accessors(fluent = true)
@Getter
public final class EnvelopeConverter {

    private final ChannelHandler requestConverter = new RequestConverter();

    private final ChannelHandler responseConverter = new ResponseConverter();

    private static final AttributeKey<URL> urlKey = AttributeKey.newInstance(Key.URL);

    static class RequestConverter extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Request request) {
                FullHttpRequest httpRequest = (FullHttpRequest) request.message();
                httpRequest.headers()
                        .add(HttpHeaderNames.HOST, request.url().address())
                        .add(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes())
                        .add(HttpHeaderNames.USER_AGENT, "Netty HttpClient")
                        .add(Key.URL, request.url());
                msg = httpRequest;
                ctx.channel().attr(urlKey).set(request.url());
            }
            ctx.write(msg, promise);
        }
    }

    static class ResponseConverter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpResponse httpResponse) {
                URL url = ctx.channel().attr(urlKey).get();
                msg = new Response(url, httpResponse);
            }
            ctx.fireChannelRead(msg);
        }
    }

}
