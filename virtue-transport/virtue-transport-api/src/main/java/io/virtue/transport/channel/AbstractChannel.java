package io.virtue.transport.channel;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.util.NetUtil;
import io.virtue.transport.endpoint.Endpoint;
import io.virtue.transport.endpoint.EndpointAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Abstract Channel.
 */
public abstract class AbstractChannel extends AbstractAccessor implements Channel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractChannel.class);

    private final Endpoint endpoint;

    protected AbstractChannel(InetSocketAddress address) {
        this.endpoint = new EndpointAdapter(address);
    }

    @Override
    public void close() {
        doClose();
        accessor.clear();
        logger.debug("Closed {}", this);
    }

    protected abstract void doClose() throws NetWorkException;

    @Override
    public InetSocketAddress remoteAddress() {
        return toInetSocketAddress();
    }

    @Override
    public String host() {
        return endpoint.host();
    }

    @Override
    public int port() {
        return endpoint.port();
    }

    @Override
    public InetSocketAddress toInetSocketAddress() {
        return endpoint.toInetSocketAddress();
    }

    @Override
    public String address() {
        return endpoint.address();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " connect to " + NetUtil.getAddress(toInetSocketAddress());
    }
}
