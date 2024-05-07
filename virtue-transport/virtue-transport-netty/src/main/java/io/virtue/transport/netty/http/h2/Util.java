package io.virtue.transport.netty.http.h2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Http2 Util.
 */
public class Util {

    /**
     * Get current channel's http2 stream.
     *
     * @param ctx
     * @param streamId
     * @param endpointUrl
     * @return
     */
    public static NettyHttp2Stream currentStream(ChannelHandlerContext ctx, int streamId, URL endpointUrl) {
        String id = String.valueOf(streamId);
        AttributeKey<NettyHttp2Stream> streamKey = AttributeKey.valueOf(id);
        Attribute<NettyHttp2Stream> streamMessageAttribute = ctx.channel().attr(streamKey);
        NettyHttp2Stream nettyHttp2Stream = streamMessageAttribute.get();
        if (nettyHttp2Stream == null) {
            URL url = new URL(endpointUrl.protocol(), endpointUrl.address());
            url.addParam(Key.ONEWAY, Boolean.FALSE.toString());
            url.addParam(Key.UNIQUE_ID, id);
            nettyHttp2Stream = new NettyHttp2Stream(url, streamId);
            streamMessageAttribute.set(nettyHttp2Stream);
        }
        return nettyHttp2Stream;
    }

    public static void removeCurrentStream(ChannelHandlerContext ctx, NettyHttp2Stream stream) {
        AttributeKey<NettyHttp2Stream> streamKey = AttributeKey.valueOf(String.valueOf(stream.streamId()));
        Attribute<NettyHttp2Stream> streamMessageAttribute = ctx.channel().attr(streamKey);
        streamMessageAttribute.set(null);
    }

    public static byte[] getSslBytes(String systemDir, String defaultPath) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String caPath = System.getProperty(systemDir);
        InputStream caStream = null;
        try {
            if (StringUtil.isBlank(caPath)) {
                caStream = classLoader.getResourceAsStream(defaultPath);
            } else {
                caStream = new FileInputStream(caPath);
            }
            if (caStream != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = caStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                return outputStream.toByteArray();
            }
            return null;
        } finally {
            if (caStream != null) {
                caStream.close();
            }
        }
    }

    /**
     * Byte array convert to InputStream.
     *
     * @param bytes
     * @return
     */
    public static InputStream readBytes(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes can't null");
        }
        return new ByteArrayInputStream(bytes);
    }
}
