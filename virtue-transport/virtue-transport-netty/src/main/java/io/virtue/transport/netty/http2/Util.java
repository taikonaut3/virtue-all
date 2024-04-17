package io.virtue.transport.netty.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.http2.envelope.StreamEnvelope;

import java.net.InetSocketAddress;

/**
 * Http2 Util.
 */
public class Util {

    public static StreamEnvelope getStreamEnvelope(ChannelHandlerContext ctx, int streamId, URL endpointUrl, boolean isServer) {
        String id = String.valueOf(streamId);
        AttributeKey<StreamEnvelope> streamKey = AttributeKey.valueOf(id);
        Attribute<StreamEnvelope> streamMessageAttribute = ctx.channel().attr(streamKey);
        StreamEnvelope streamEnvelope = streamMessageAttribute.get();
        if (streamEnvelope == null) {
            InetSocketAddress address = (InetSocketAddress) (isServer ? ctx.channel().localAddress() : ctx.channel().remoteAddress());
            URL url = new URL(endpointUrl.protocol(), address);
            url.addParam(Key.ONEWAY, Boolean.FALSE.toString());
            url.addParam(Key.UNIQUE_ID, id);
            streamEnvelope = new StreamEnvelope(url, streamId);
            streamMessageAttribute.set(streamEnvelope);
        }
        return streamEnvelope;
    }

    public static void removeStreamEnvelope(ChannelHandlerContext ctx, int streamId) {
        AttributeKey<StreamEnvelope> streamKey = AttributeKey.valueOf(String.valueOf(streamId));
        Attribute<StreamEnvelope> streamMessageAttribute = ctx.channel().attr(streamKey);
        streamMessageAttribute.set(null);
    }
}
