package io.virtue.transport.netty.http.h1.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import io.virtue.transport.Request;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;

import static io.virtue.common.constant.Components.Protocol.HTTP;

/**
 * EnvelopeConverter.
 */
@Getter
@Accessors(fluent = true)
public final class HttpServerMessageConverter {

    private static final AttributeKey<URL> urlKey = AttributeKey.newInstance(Key.URL);
    private final ChannelHandler requestConverter = new RequestConverter();

    static class RequestConverter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpRequest httpRequest) {
                URL url = new URL(HTTP, NetUtil.getAddress((InetSocketAddress) ctx.channel().localAddress()));
                url.addParam(Key.ONEWAY, Boolean.FALSE.toString());
                msg = new Request(url, httpRequest);
            }
            ctx.fireChannelRead(msg);
        }
    }

}
