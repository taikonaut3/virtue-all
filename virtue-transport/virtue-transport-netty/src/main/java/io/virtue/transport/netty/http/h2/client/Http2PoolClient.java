package io.virtue.transport.netty.http.h2.client;

import io.netty.channel.Channel;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettySupport;
import io.virtue.transport.netty.client.NettyPoolClient;

/**
 * Http2 Pool Client.
 */
public class Http2PoolClient extends NettyPoolClient {
    public Http2PoolClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);
    }

    @Override
    protected void doSend(Channel channel, Object message) {
        Http2StreamChannelBootstrap streamChannelBootstrap = channel.attr(NettySupport.H2_STREAM_BOOTSTRAP_KEY).get();
        Http2StreamChannel streamChannel = NettySupport.newStreamChannel(streamChannelBootstrap, url);
        streamChannel.writeAndFlush(message);
        release(channel);
    }
}

