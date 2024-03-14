package io.virtue.transport.netty;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.client.NettyClient;
import io.virtue.transport.netty.server.NettyServer;
import io.virtue.transport.server.Server;
import io.virtue.common.constant.Components;

@ServiceProvider(Components.Transport.NETTY)
public final class NettyTransport implements Transporter {

    @Override
    public Client connect(URL url, ChannelHandler handler, Codec codec) {
        return new NettyClient(url, handler, codec);
    }

    @Override
    public Server bind(URL url, ChannelHandler handler, Codec codec) {
        return new NettyServer(url, handler, codec);
    }

}
