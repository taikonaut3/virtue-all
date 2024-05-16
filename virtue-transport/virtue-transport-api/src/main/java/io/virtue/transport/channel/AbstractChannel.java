package io.virtue.transport.channel;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.AbstractAccessor;

import java.net.InetSocketAddress;

import static io.virtue.common.util.NetUtil.getAddress;
import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract Channel.
 */
public abstract class AbstractChannel extends AbstractAccessor implements Channel {

    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;

    protected AbstractChannel(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void close() {
        if (isActive()) {
            doClose();
            accessor.clear();
        }
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
        return String.format("%s %s connect to %s", simpleClassName(this), getAddress(localAddress), getAddress(remoteAddress));
    }
}
