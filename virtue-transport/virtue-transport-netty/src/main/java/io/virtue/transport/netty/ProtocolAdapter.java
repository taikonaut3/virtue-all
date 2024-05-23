package io.virtue.transport.netty;

import io.netty.channel.*;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.SslContext;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.netty.client.NettyClient;
import io.virtue.transport.netty.client.NettyPoolClient;
import io.virtue.transport.netty.custom.CustomChannelInitializer;
import io.virtue.transport.netty.custom.CustomClientChannelInitializer;
import io.virtue.transport.netty.custom.NettyCustomCodec;
import io.virtue.transport.netty.http.h1.NettyHttpHeaders;
import io.virtue.transport.netty.http.h1.client.HttpClient;
import io.virtue.transport.netty.http.h1.client.HttpClientChannelPoolInitializer;
import io.virtue.transport.netty.http.h1.server.HttpServer;
import io.virtue.transport.netty.http.h1.server.HttpServerChannelInitializer;
import io.virtue.transport.netty.http.h2.NettyHttp2Headers;
import io.virtue.transport.netty.http.h2.client.Http2Client;
import io.virtue.transport.netty.http.h2.client.Http2ClientChannelInitializer;
import io.virtue.transport.netty.http.h2.client.Http2ClientPoolChannelInitializer;
import io.virtue.transport.netty.http.h2.client.Http2PoolClient;
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
    public static ChannelInitializer<SocketChannel> acquireClientChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
        String protocol = url.protocol();
        // http1.1 use HttpChannelPoolHandler init channel
        return switch (protocol) {
            case H2, H2C -> new Http2ClientChannelInitializer(url, handler);
            default -> new CustomChannelInitializer(url, handler, codec, false);
        };
    }

    /**
     * Acquire client channelPoolHandler.
     *
     * @param url
     * @param handler
     * @param client
     * @param codec
     * @return
     */
    public static ChannelPoolHandler acquireClientChannelPoolHandler(URL url, ChannelHandler handler, NettyPoolClient client, Codec codec) {
        String protocol = url.protocol();
        return switch (protocol) {
            case HTTP, HTTPS -> new HttpClientChannelPoolInitializer(url, handler, client);
            case H2, H2C -> new Http2ClientPoolChannelInitializer(url, handler, client);
            default -> new CustomClientChannelInitializer(url, handler, codec);
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
    public static ChannelInitializer<SocketChannel> acquireServerChannelInitializer(URL url, ChannelHandler handler, Codec codec) {
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
            case HTTP, HTTPS -> new HttpServer(url, handler, codec);
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
        int maxConnections = url.getIntParam(Key.MAX_CONNECTIONS, 1);
        return switch (protocol) {
            case HTTP, HTTPS -> new HttpClient(url, handler, codec);
            case H2, H2C ->
                    maxConnections == 1 ? new Http2Client(url, handler, codec) : new Http2PoolClient(url, handler, codec);
            default ->
                    maxConnections == 1 ? new NettyClient(url, handler, codec) : new NettyPoolClient(url, handler, codec);
        };
    }

    /**
     * Config client channel pipeline.
     *
     * @param url
     * @param channel
     * @param sslContext
     * @param codec
     * @param idleStateHandler
     * @param handlers
     */
    public static void configClientChannelPipeline(URL url, Channel channel, SslContext sslContext, Codec codec,
                                                   NettyIdleStateHandler idleStateHandler, ChannelHandler... handlers) {
        String protocol = url.protocol();
        ChannelPipeline pipeline = channel.pipeline();
        switch (protocol) {
            case HTTP, HTTPS -> {
                int maxReceiveSize = url.getIntParam(Key.CLIENT_MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
                if (sslContext != null) {
                    pipeline.addLast("ssl", sslContext.newHandler(channel.alloc()));
                }
                pipeline.addLast("heartbeat", idleStateHandler)
                        .addLast("heartbeatHandler", idleStateHandler.handler())
                        .addLast("httpClientCodec", new HttpClientCodec())
                        .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                        .addLast(handlers);
            }
            case H2, H2C -> {
                if (sslContext != null) {
                    pipeline.addLast(sslContext.newHandler(channel.alloc()));
                }
                final Http2FrameCodec http2FrameCodec = Http2FrameCodecBuilder.forClient()
                        // this is the default, but shows it can be changed.
                        .initialSettings(Http2Settings.defaultSettings())
                        .build();
                pipeline.addLast(idleStateHandler)
                        .addLast(idleStateHandler.handler())
                        .addLast(http2FrameCodec)
                        // this parameter ChannelInboundHandlerAdapter is Invalid for client
                        .addLast(new Http2MultiplexHandler(new ChannelInboundHandlerAdapter()));
                var streamChannelBootstrap = new Http2StreamChannelBootstrap(channel);
                streamChannelBootstrap.handler(handlers[0]);
                channel.attr(NettySupport.H2_STREAM_BOOTSTRAP_KEY).set(streamChannelBootstrap);
            }
            default -> {
                NettyCustomCodec nettyCustomCodec = new NettyCustomCodec(url, codec, false);
                pipeline.addLast("decoder", nettyCustomCodec.getDecoder())
                        .addLast("encoder", nettyCustomCodec.getEncoder())
                        .addLast("idleState", idleStateHandler)
                        .addLast("heartbeat", idleStateHandler.handler())
                        .addLast(handlers);
            }
        }
    }

    /**
     * Config server channel pipeline.
     *
     * @param url
     * @param channel
     * @param sslContext
     * @param codec
     * @param idleStateHandler
     * @param handlers
     */
    public static void configServerChannelPipeline(URL url, Channel channel, SslContext sslContext, Codec codec,
                                                   NettyIdleStateHandler idleStateHandler, ChannelHandler... handlers) {
        String protocol = url.protocol();
        ChannelPipeline pipeline = channel.pipeline();
        switch (protocol) {
            case HTTP, HTTPS -> {
                int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
                if (sslContext != null) {
                    pipeline.addLast("ssl", sslContext.newHandler(channel.alloc()));
                }
                pipeline.addLast("heartbeat", idleStateHandler)
                        .addLast("heartbeatHandler", idleStateHandler.handler())
                        .addLast("httpServerCodec", new HttpServerCodec())
                        .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                        .addLast(handlers);
            }
            default -> {
                NettyCustomCodec nettyCustomCodec = new NettyCustomCodec(url, codec, true);
                pipeline.addLast("decoder", nettyCustomCodec.getDecoder())
                        .addLast("encoder", nettyCustomCodec.getEncoder())
                        .addLast("idleState", idleStateHandler)
                        .addLast("heartbeat", idleStateHandler.handler())
                        .addLast(handlers);
            }
        }
    }
}