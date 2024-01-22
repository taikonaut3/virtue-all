package io.github.astro.virtue.transport.channel;

import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.common.util.NetUtil;
import io.github.astro.virtue.transport.endpoint.EndpointAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractChannel extends EndpointAdapter implements Channel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractChannel.class);

    private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();

    protected AbstractChannel(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void close() throws NetWorkException {
        doClose();
        attributeMap.clear();
        logger.debug("Closed {}", this);
    }

    protected abstract void doClose() throws NetWorkException;

    @Override
    public InetSocketAddress remoteAddress() {
        return toInetSocketAddress();
    }

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public Object getAttribute(String key, Object defaultValue) {
        Object result = getAttribute(key);
        if (result == null) {
            attributeMap.put(key, defaultValue);
            result = defaultValue;
        }
        return result;
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " connect to " + NetUtil.getAddress(toInetSocketAddress());
    }

}
