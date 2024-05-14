package io.virtue.transport.netty;

import io.virtue.common.extension.spi.Extension;
import io.virtue.core.Virtue;
import io.virtue.core.VirtueConfiguration;
import io.virtue.transport.netty.client.NettyClient;

@Extension("clientEventGroupCloser")
public class ClientEventGroupCloser implements VirtueConfiguration {

    @Override
    public void stopAfter(Virtue virtue) {
        NettyClient.closeNioEventLoopGroup();
    }
}
