package io.virtue.transport.netty.http.h1.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;

/**
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    public HttpClientChannelInitializer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        NettyIdleStateHandler idleStateHandler = NettyIdleStateHandler.createForClient(url);
        int maxReceiveSize = url.getIntParam(Key.CLIENT_MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        HttpClientMessageConverter converter = new HttpClientMessageConverter();
        socketChannel.pipeline()
                .addLast("heartbeat", idleStateHandler)
                .addLast("heartbeatHandler", idleStateHandler.handler())
                .addLast("httpClientCodec", new HttpClientCodec())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast("responseConverter", converter.responseConverter())
                .addLast("requestConverter", converter.requestConverter())
                .addLast("handler", handler);
    }
}
