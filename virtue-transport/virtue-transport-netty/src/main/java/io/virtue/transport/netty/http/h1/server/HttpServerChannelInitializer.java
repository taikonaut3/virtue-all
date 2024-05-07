package io.virtue.transport.netty.http.h1.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;

/**
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    public HttpServerChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForServer(url);
        int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        HttpServerMessageConverter converter = new HttpServerMessageConverter(url);
        socketChannel.pipeline()
                .addLast("heartbeat", idleStateHandler)
                .addLast("heartbeatHandler", idleStateHandler.handler())
                .addLast("httpServerCodec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast("requestConverter", converter.requestConverter())
                .addLast("responseConvert", converter.responseConvert())
                .addLast("handler", handler);
    }
}
