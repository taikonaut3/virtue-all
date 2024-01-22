package io.github.astro.virtue.config.config;

import io.github.astro.virtue.common.url.Parameterization;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.NetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public abstract class UrlTypeConfig implements Parameterization {

    protected String type;

    protected String name;

    protected String host;

    protected int port;

    protected ConfigScope scope = ConfigScope.NONE;

    public abstract URL toUrl();

    public String getAddress() {
        return NetUtil.getAddress(host, port);
    }

    public void setAddress(String address) {
        InetSocketAddress inetSocketAddress = NetUtil.toInetSocketAddress(address);
        this.host = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }

    @Override
    public String toString() {
        return toUrl().toString();
    }
}
