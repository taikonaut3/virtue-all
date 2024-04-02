package io.virtue.transport.netty.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * EnvelopeConverter.
 */
@Getter
@Accessors(fluent = true)
public final class HttpMessageConverter {

    private static final AttributeKey<URL> urlKey = AttributeKey.newInstance(Key.URL);
    private final ChannelHandler requestConverter = new RequestConverter();
    private final ChannelHandler responseConverter = new ResponseConverter();

    static class RequestConverter extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Request request) {
                HttpRequest httpRequest = (HttpRequest) request.message();
                httpRequest.headers()
                        .add(HttpHeaderNames.HOST, request.url().address())
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
                msg = Response.success(url, httpResponse);
            }
            ctx.fireChannelRead(msg);
        }
    }

}
