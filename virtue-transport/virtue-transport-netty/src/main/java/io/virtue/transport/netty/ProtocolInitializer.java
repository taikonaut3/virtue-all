package io.virtue.transport.netty;

import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.custom.CustomChannelInitializer;
import io.virtue.transport.netty.http.HttpChannelInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.virtue.common.constant.Components;

public class ProtocolInitializer {

    public static ChannelInitializer<SocketChannel> getInitializer(URL url, ChannelHandler handler, Codec codec, boolean isServer) {
        String protocol = url.protocol();
        switch (protocol) {
            case Components.Protocol.HTTP:
                return new HttpChannelInitializer(url, handler, isServer);
            default:
                return new CustomChannelInitializer(url, handler, codec, isServer);
        }
    }
}