package io.github.astro.virtue.transport.netty;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.AbstractTransport;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.netty.client.NettyClient;
import io.github.astro.virtue.transport.netty.server.NettyServer;
import io.github.astro.virtue.transport.server.Server;

import static io.github.astro.virtue.common.constant.Components.Transport.NETTY;

@ServiceProvider(NETTY)
public final class NettyTransport extends AbstractTransport {

    @Override
    protected Client createClient(URL url, ChannelHandler handler, Codec codec) {
        return new NettyClient(url, handler, codec);
    }

    @Override
    protected Server createServer(URL url, ChannelHandler handler, Codec codec) {
        return new NettyServer(url, handler, codec);
    }

}
