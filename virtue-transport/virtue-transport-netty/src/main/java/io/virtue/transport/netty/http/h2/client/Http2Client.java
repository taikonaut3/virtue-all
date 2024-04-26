package io.virtue.transport.netty.http.h2.client;

import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.url.URL;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.NettyChannel;
import io.virtue.transport.netty.client.NettyClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http2Client Base on Netty.
 */
public class Http2Client extends NettyClient {

    private Http2StreamChannelBootstrap streamChannelBootstrap;

    private final Map<Http2StreamChannel, RpcFuture> streamChannelFutureMap = new ConcurrentHashMap<>();

    public Http2Client(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url, channelHandler, codec);

    }

    @Override
    protected void doConnect() throws ConnectException {
        super.doConnect();
        streamChannelBootstrap = new Http2StreamChannelBootstrap(channel);
        Http2ClientHandler http2ClientHandler = new Http2ClientHandler(this, url, nettyHandler);
        streamChannelBootstrap.handler(http2ClientHandler);
    }

    @Override
    public void send(Object message) {
        Http2StreamChannel streamChannel = newStreamChannel();
        if (message instanceof Http2Frame frame) {
            streamChannel.writeAndFlush(frame);
        }
    }

    /**
     * Send the frames to the server.
     *
     * @param future
     * @param frames
     */
    public void send(RpcFuture future, Http2Frame... frames) {
        Http2StreamChannel streamChannel = newStreamChannel();
        for (Http2Frame frame : frames) {
            streamChannel.writeAndFlush(frame);
        }
        streamChannelFutureMap.put(streamChannel, future);
    }

    public RpcFuture getRpcFuture(Http2StreamChannel streamChannel) {
        RpcFuture rpcFuture = streamChannelFutureMap.get(streamChannel);
        streamChannelFutureMap.remove(streamChannel);
        return rpcFuture;
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
