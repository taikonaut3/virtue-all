package io.virtue.transport.netty.http.h1.client;

import io.netty.channel.Channel;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.client.NettyPoolClient;

/**
 * Http Client.
 */
public class HttpClient extends NettyPoolClient {

    public HttpClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);
    }

    @Override
    protected void doSend(Channel channel, Object message) {
        channel.writeAndFlush(message);
    }
}
