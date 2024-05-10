package io.virtue.transport.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.virtue.common.url.URL;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.netty.client.NettyClient;
import io.virtue.transport.netty.custom.CustomChannelInitializer;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;
import io.virtue.transport.netty.http.h1.client.HttpClientChannelInitializer;
import io.virtue.transport.netty.http.h1.server.HttpServerChannelInitializer;
import io.virtue.transport.netty.http.h2.NettyHttp2Headers;
import io.virtue.transport.netty.http.h2.client.Http2Client;
import io.virtue.transport.netty.http.h2.client.Http2ClientChannelInitializer;
import io.virtue.transport.netty.http.h2.server.Http2Server;
import io.virtue.transport.netty.http.h2.server.Http2ServerChannelInitializer;
import io.virtue.transport.netty.server.NettyServer;
import io.virtue.transport.server.Server;

import java.util.Map;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Initializer SocketChannel for protocol.
 */
public class ProtocolAdapter {

    /**
     * ChannelInitializer for client.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     */
    public static ChannelInitializer<SocketChannel> forClientChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case HTTP, HTTPS -> new HttpClientChannelInitializer(url, handler);
            case H2, H2C -> new Http2ClientChannelInitializer(url);
            default -> new CustomChannelInitializer(url, handler, codec, false);
        };
    }

    /**
     * ChannelInitializer for server.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     */
    public static ChannelInitializer<SocketChannel> forServerChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case HTTP, HTTPS -> new HttpServerChannelInitializer(url, handler);
            case H2, H2C -> new Http2ServerChannelInitializer(url, handler);
            default -> new CustomChannelInitializer(url, handler, codec, true);
        };
    }

    /**
     * Create http headers by http protocol version.
     *
     * @param version
     * @param headers
     * @return
     */
    public static HttpHeaders buildHttpHeaders(HttpVersion version, Map<CharSequence, CharSequence> headers) {
        return switch (version) {
            case HTTP_1_0 -> throw new UnsupportedOperationException("unSupport http1.0");
            case HTTP_2_0 -> new NettyHttp2Headers(headers);
            default -> new NettyHttpHeaders(headers);
        };
    }

    /**
     * Create server by protocol.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     */
    public static Server bindServer(URL url, io.virtue.transport.channel.ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case H2, H2C -> new Http2Server(url, handler, codec);
            default -> new NettyServer(url, handler, codec);
        };
    }

    /**
     * Create client by protocol.
     *
     * @param url
     * @param handler
     * @param codec
     * @return
     */
    public static Client connectClient(URL url, io.virtue.transport.channel.ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case H2, H2C -> new Http2Client(url, handler, codec);
            default -> new NettyClient(url, handler, codec);
        };
    }
}