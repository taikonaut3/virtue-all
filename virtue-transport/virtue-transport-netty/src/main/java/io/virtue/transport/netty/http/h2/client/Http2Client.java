package io.virtue.transport.netty.http.h2.client;

import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.client.NettyClient;

/**
 * Http2Client Base on Netty.
 */
public class Http2Client extends NettyClient {

    private Http2StreamChannelBootstrap streamChannelBootstrap;

    public Http2Client(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);

    }

    @Override
    protected void doConnect() throws ConnectException {
        super.doConnect();
        streamChannelBootstrap = channel.attr(NettySupport.H2_STREAM_BOOTSTRAP_KEY).get();
    }

    @Override
    protected void doInit() throws ConnectException {
        super.doInit();
    }

    @Override
    public void send(Object message) {
        Http2StreamChannel streamChannel = NettySupport.newStreamChannel(streamChannelBootstrap, url);
        streamChannel.writeAndFlush(message);
    }
}
