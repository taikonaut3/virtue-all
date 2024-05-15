package io.virtue.transport.channel;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Abstract Channel.
 */
public abstract class AbstractChannel extends AbstractAccessor implements Channel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractChannel.class);

    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;

    protected AbstractChannel(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void close() {
        doClose();
        accessor.clear();
        logger.debug("Closed {}", this);
    }

    protected abstract void doClose() throws NetWorkException;

    @Override
    public InetSocketAddress localAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return remoteAddress;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + NetUtil.getAddress(localAddress) + " connect to " + NetUtil.getAddress(remoteAddress);
    }
}
