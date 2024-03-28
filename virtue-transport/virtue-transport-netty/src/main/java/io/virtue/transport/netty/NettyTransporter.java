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

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Base on netty's transporter.
 */
@ServiceProvider(NETTY)
public final class NettyTransporter implements Transporter {

    @Override
    public Client connect(URL url, ChannelHandler handler, Codec codec) {
        return new NettyClient(url, handler, codec);
    }

    @Override
    public Server bind(URL url, ChannelHandler handler, Codec codec) {
        return new NettyServer(url, handler, codec);
    }

}
