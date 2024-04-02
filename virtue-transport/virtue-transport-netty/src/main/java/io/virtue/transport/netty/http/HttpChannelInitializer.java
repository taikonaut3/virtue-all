package io.virtue.transport.netty.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdeStateHandler;

/**
 * Initializes the channel of Netty for HTTP Codec.
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final URL url;

    private final ChannelHandler handler;

    private final boolean isServer;

    public HttpChannelInitializer(URL url, ChannelHandler handler, boolean isServer) {
        this.url = url;
        this.handler = handler;
        this.isServer = isServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        int keepAliveTimeout = url.getIntParam(Key.KEEP_ALIVE_TIMEOUT, Constant.DEFAULT_KEEP_ALIVE_TIMEOUT);
        IdleStateHandler idleStateHandler = NettyIdeStateHandler.create(keepAliveTimeout, isServer);
        initClientPipeline(socketChannel, idleStateHandler);
    }

    private void initClientPipeline(SocketChannel socketChannel, IdleStateHandler idleStateHandler) {
        String maxMessageKey = isServer ? Key.MAX_RECEIVE_SIZE : Key.CLIENT_MAX_RECEIVE_SIZE;
        int maxReceiveSize = url.getIntParam(maxMessageKey, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        HttpMessageConverter converter = new HttpMessageConverter();
        socketChannel.pipeline()
                .addLast("httpClientCodec", new HttpClientCodec())
                .addLast("requestConverter", converter.requestConverter())
                .addLast("aggregator", new HttpObjectAggregator(maxReceiveSize))
                .addLast("responseConverter", converter.responseConverter())
                .addLast("heartbeat", idleStateHandler)
                .addLast("handler", handler);
    }
}
