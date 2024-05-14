package io.virtue.transport.netty.http.h1.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.VirtueHttpHeaderNames;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;

import java.util.Optional;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.DefaultHttpHeadersFactory.trailersFactory;

/**
 * Http server message converter.
 */

public final class HttpServerMessageConverter {

    @Sharable
    public static class RequestConverter extends ChannelInboundHandlerAdapter {
        private final URL url;

        public RequestConverter(URL url) {
            this.url = url;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpRequest fullHttpRequest) {
                String path = URL.parsePath(fullHttpRequest.uri());
                var queryParams = URL.parseParams(fullHttpRequest.uri());
                URL requestUrl = new URL(this.url.protocol(), this.url.address());
                requestUrl.addPaths(URL.pathToList(path));
                Optional.ofNullable(queryParams)
                        .ifPresent(params -> params.forEach((key, value) -> requestUrl.addParam(key.toString(), value.toString())));
                requestUrl.addParam(Key.ONEWAY, Boolean.FALSE.toString());
                NettyHttpRequest httpRequest = new NettyHttpRequest(
                        HttpVersion.HTTP_1_1,
                        requestUrl,
                        new NettyHttpHeaders(fullHttpRequest.headers()),
                        NettySupport.getBytes(fullHttpRequest.content()));
                CharSequence virtueUrlStr = httpRequest.headers().get(VirtueHttpHeaderNames.VIRTUE_URL);
                URL url = requestUrl;
                if (!StringUtil.isBlank(virtueUrlStr)) {
                    url = URL.valueOf(virtueUrlStr.toString());
                }
                msg = new Request(url, httpRequest);
                fullHttpRequest.release();
            }
            ctx.fireChannelRead(msg);
        }
    }

    @Sharable
    public static class ResponseConvert extends ChannelOutboundHandlerAdapter {
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
