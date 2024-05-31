package io.virtue.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.transport.netty.client.NettyPoolClient;
import io.virtue.transport.netty.http.h1.client.HttpClientMessageConverter;
import io.virtue.transport.netty.http.h1.server.HttpServerMessageConverter;
import io.virtue.transport.netty.http.h2.NettyHttp2Stream;
import io.virtue.transport.netty.http.h2.client.Http2ClientMessageConverter;
import io.virtue.transport.netty.http.h2.server.Http2ServerMessageConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Netty support.
 */
public class NettySupport {

    public static final AttributeKey<Http2StreamChannelBootstrap> H2_STREAM_BOOTSTRAP_KEY = AttributeKey.newInstance("h2-stream-bootstrap");

    private static final AttributeKey<URL> URL_KEY = AttributeKey.newInstance(Key.URL);

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
            URL url = getUrlFormChannel(ctx.channel());
            if (url == null) {
                //server endpoint
                url = new URL(endpointUrl.protocol(), endpointUrl.address());
            }
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

    /**
     * Convert to http2 stream frames.
     *
     * @param headers
     * @param data
     * @return
     */
    public static Http2StreamFrame[] convertToHttp2StreamFrames(Http2Headers headers, ByteBuf data) {
        boolean headersEndStream = !data.isReadable();
        Http2StreamFrame headersFrame = new DefaultHttp2HeadersFrame(headers, headersEndStream);
        Http2StreamFrame dataFrame = headersEndStream ? null : new DefaultHttp2DataFrame(data, true);
        return headersEndStream ? new Http2StreamFrame[]{headersFrame} : new Http2StreamFrame[]{headersFrame, dataFrame};
    }

    /**
     * Bind url to current channel.
     *
     * @param url
     * @param channel
     */
    public static void bindUrlToChannel(URL url, Channel channel) {
        channel.attr(URL_KEY).set(url);
    }

    /**
     * Remove url from current channel.
     *
     * @param channel
     */
    public static void removeUrlFromChannel(Channel channel) {
        channel.attr(URL_KEY).set(null);
    }

    /**
     * Get url from current channel.
     *
     * @param channel
     * @return
     */
    public static URL getUrlFormChannel(Channel channel) {
        return channel.attr(URL_KEY).get();
    }

    /**
     * Get ssl bytes.
     *
     * @param systemDir
     * @param defaultPath
     * @return
     * @throws Exception
     */
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

    /**
     * ByteBuf convert to byte[].
     *
     * @param buf
     * @return
     */
    public static byte[] getBytes(ByteBuf buf) {
        return io.netty.buffer.ByteBufUtil.getBytes(buf, buf.readerIndex(), buf.readableBytes(), false);
    }

    /**
     * Create http client handlers.
     *
     * @param client
     * @param adapterHandler
     * @return
     */
    public static ChannelHandler[] createHttpClientHandlers(NettyPoolClient client, ChannelHandler adapterHandler) {
        return new ChannelHandler[]{
                new HttpClientMessageConverter.RequestConverter(),
                new HttpClientMessageConverter.ResponseConverter(client),
                adapterHandler
        };
    }

    /**
     * Create http server handlers.
     *
     * @param serverUrl
     * @param adapterHandler
     * @return
     */
    public static ChannelHandler[] createHttpServerHandlers(URL serverUrl, ChannelHandler adapterHandler) {
        return new ChannelHandler[]{
                new HttpServerMessageConverter.RequestConverter(serverUrl),
                new HttpServerMessageConverter.ResponseConvert(),
                adapterHandler
        };
    }

    /**
     * Create http2 client handlers.
     *
     * @param adapterHandler
     * @return
     */
    public static ChannelHandler[] createHttp2ClientHandlers(ChannelHandler adapterHandler) {
        return new ChannelHandler[]{
                new Http2ClientMessageConverter.RequestConverter(),
                new Http2ClientMessageConverter.ResponseConverter(),
                adapterHandler
        };
    }

    /**
     * Create http2 server handlers.
     *
     * @param adapterHandler
     * @return
     */
    public static ChannelHandler[] createHttp2ServerHandlers(ChannelHandler adapterHandler) {
        return new ChannelHandler[]{
                new Http2ServerMessageConverter.RequestConverter(),
                new Http2ServerMessageConverter.ResponseConverter(),
                adapterHandler
        };
    }

    /**
     * Create http2 stream channel.
     *
     * @param bootstrap
     * @param url
     * @return
     */
    public static Http2StreamChannel newStreamChannel(Http2StreamChannelBootstrap bootstrap, URL url) {
        Http2StreamChannel streamChannel = bootstrap.open().syncUninterruptibly().getNow();
        NettyChannel nettyChannel = NettyChannel.getChannel(streamChannel);
        nettyChannel.set(URL.ATTRIBUTE_KEY, url);
        return streamChannel;
    }
}
