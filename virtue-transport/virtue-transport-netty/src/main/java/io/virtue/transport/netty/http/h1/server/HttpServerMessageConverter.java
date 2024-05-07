package io.virtue.transport.netty.http.h1.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.netty.ByteBufUtil;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Optional;

import static io.netty.handler.codec.http.DefaultHttpHeadersFactory.trailersFactory;

/**
 * EnvelopeConverter.
 */
@Getter
@Accessors(fluent = true)
public final class HttpServerMessageConverter {

    private static final AttributeKey<URL> urlKey = AttributeKey.newInstance(Key.URL);
    private final ChannelHandler requestConverter;
    private final ChannelHandler responseConvert;

    public HttpServerMessageConverter(URL url) {
        this.requestConverter = new RequestConverter(url);
        this.responseConvert = new ResponseConvert();
    }

    static class RequestConverter extends ChannelInboundHandlerAdapter {
        private final URL url;

        public RequestConverter(URL url) {
            this.url = url;
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpRequest fullHttpRequest) {
                String uri = URL.parsePath(fullHttpRequest.uri());
                URL requestUrl = URL.valueOf(uri);
                Optional.ofNullable(URL.parseParams(fullHttpRequest.uri()))
                        .ifPresent(params -> params.forEach((key, value) -> requestUrl.addParam(key.toString(), value.toString())));
                requestUrl.addParam(Key.ONEWAY, Boolean.FALSE.toString());
                NettyHttpRequest httpRequest = new NettyHttpRequest(
                        HttpVersion.HTTP_1_1,
                        requestUrl,
                        new NettyHttpHeaders(fullHttpRequest.headers()),
                        ByteBufUtil.getBytes(fullHttpRequest.content()));
                msg = new Request(requestUrl, httpRequest);
            }
            ctx.fireChannelRead(msg);
        }
    }

    static class ResponseConvert extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof Response response) {
                HttpResponse httpResponse = (HttpResponse) response.message();
                DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                        io.netty.handler.codec.http.HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(httpResponse.statusCode()),
                        Unpooled.wrappedBuffer(httpResponse.data()),
                        ((NettyHttpHeaders) httpResponse.headers()).httpHeaders(),
                        trailersFactory().newHeaders()
                );
                ctx.write(fullHttpResponse, promise);
            }
        }
    }

}
