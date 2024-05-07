package io.virtue.transport.netty;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Base on netty's transporter.
 */
@Extension(NETTY)
public class NettyTransporter implements Transporter {

    @Override
    public Client connect(URL url, ChannelHandler handler, Codec codec) {
        return ProtocolAdapter.connectClient(url, handler, codec);
    }

    @Override
    public Server bind(URL url, ChannelHandler handler, Codec codec) {
        return ProtocolAdapter.bindServer(url, handler, codec);
    }

}
