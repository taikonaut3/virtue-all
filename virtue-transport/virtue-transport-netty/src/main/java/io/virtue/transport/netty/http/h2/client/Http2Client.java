package io.virtue.transport.netty.http.h2.client;

import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.client.NettyClient;

/**
 * Http2Client Base on Netty.
 */
public class Http2Client extends NettyClient {

    private Http2StreamChannelBootstrap streamChannelBootstrap;
    private io.netty.channel.ChannelHandler[] handlers;

    public Http2Client(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);

    }

    @Override
    protected void doInit() throws ConnectException {
        super.doInit();
        handlers = NettySupport.createHttp2ClientHandlers(nettyHandler);
    }

    @Override
    protected void doConnect() throws ConnectException {
        super.doConnect();
        streamChannelBootstrap = new Http2StreamChannelBootstrap(channel);
        Http2ClientHandler http2ClientHandler = new Http2ClientHandler(url, handlers);
        streamChannelBootstrap.handler(http2ClientHandler);
    }

    @Override
    public void send(Object message) {
        Http2StreamChannel streamChannel = newStreamChannel();
        streamChannel.writeAndFlush(message);
    }

    /**
     * Get the new Http2StreamChannel.
     *
     * @return
     */
    public Http2StreamChannel newStreamChannel() {
        Http2StreamChannel streamChannel = streamChannelBootstrap.open().syncUninterruptibly().getNow();
        NettyChannel nettyChannel = NettyChannel.getChannel(streamChannel);
        nettyChannel.set(URL.ATTRIBUTE_KEY, url);
        return streamChannel;
    }
}
