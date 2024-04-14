package io.virtue.transport.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.custom.CustomChannelInitializer;
import io.virtue.transport.netty.http.client.HttpClientChannelInitializer;
import io.virtue.transport.netty.http.server.HttpServerChannelInitializer;
import io.virtue.transport.netty.http2.client.Http2ClientChannelInitializer;
import io.virtue.transport.netty.http2.server.Http2ServerChannelInitializer;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Initializer SocketChannel for protocol.
 */
public class ProtocolInitializer {

    public static ChannelInitializer<SocketChannel> getForClient(URL url, ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case HTTP -> new HttpClientChannelInitializer(url, handler);
            case HTTP2, H2, H2C -> new Http2ClientChannelInitializer(url);
            default -> new CustomChannelInitializer(url, handler, codec, false);
        };
    }

    public static ChannelInitializer<SocketChannel> getForServer(URL url, ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case HTTP -> new HttpServerChannelInitializer(url, handler);
            case HTTP2, H2, H2C -> new Http2ServerChannelInitializer(url, handler);
            default -> new CustomChannelInitializer(url, handler, codec, true);
        };
    }
}