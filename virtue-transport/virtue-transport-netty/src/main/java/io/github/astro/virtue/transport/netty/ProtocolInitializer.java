package io.github.astro.virtue.transport.netty;

import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.netty.custom.CustomChannelInitializer;
import io.github.astro.virtue.transport.netty.http.HttpChannelInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

public class ProtocolInitializer {

    public static ChannelInitializer<SocketChannel> getInitializer(URL url, ChannelHandler handler, Codec codec, boolean isServer) {
        String protocol = url.protocol();
        switch (protocol) {
            case HTTP:
                return new HttpChannelInitializer(url, handler, isServer);
            default:
                return new CustomChannelInitializer(url, handler, codec);
        }
    }
}