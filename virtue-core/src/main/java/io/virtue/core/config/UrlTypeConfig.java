package io.virtue.core.config;

import io.virtue.common.url.Parameterization;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;

/**
 * Url Type Config can convert URL.
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public abstract class UrlTypeConfig implements Parameterization {

    protected String type;

    protected String name;

    protected String host;

    protected int port;

    /**
     * Convert to URL.
     *
     * @return
     */
    public abstract URL toUrl();

    public String getAddress() {
        return NetUtil.getAddress(host, port);
    }

    /**
     * Set address.
     *
     * @param address
     * @return
     */
    public UrlTypeConfig address(String address) {
        InetSocketAddress inetSocketAddress = NetUtil.toInetSocketAddress(address);
        this.host = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
        return this;
    }

    @Override
    public String toString() {
        return toUrl().toString();
    }
}
