package io.github.astro.virtue.config;

import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.NetUtil;
import io.github.astro.virtue.common.util.StringUtil;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class RemoteUrl extends URL {

    private String remoteApplication;

    public RemoteUrl(String protocol, String address) {
        AssertUtil.assertNotBlank(address, "Address can't empty");
        protocol(protocol);
        try {
            InetSocketAddress socketAddress = NetUtil.toInetSocketAddress(address);
            host(socketAddress.getHostString());
            port(socketAddress.getPort());
        } catch (IllegalArgumentException e) {
            remoteApplication = address;
        }
    }

    @Override
    public String authority() {
        if (!StringUtil.isBlank(remoteApplication)) {
            return protocol + "://" + remoteApplication;
        }
        return protocol + "://" + host + ":" + port;
    }
}
