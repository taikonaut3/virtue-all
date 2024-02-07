package io.github.astro.virtue.rpc.http.spring;

import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.VirtueConfiguration;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.rpc.http1_1.HttpParser;
import io.github.astro.virtue.rpc.http1_1.HttpProtocol;
import io.github.astro.virtue.rpc.protocol.Protocol;

@ServiceProvider("springWebMethodParserRegister")
public class SpringWebMethodParserRegister implements VirtueConfiguration {

    @Override
    public void initBefore(Virtue virtue) {
        ExtensionLoader.addListener(Protocol.class, protocol -> {
            if (protocol instanceof HttpProtocol) {
                HttpParser httpParser = (HttpParser) protocol.parser();
                httpParser.methodParser(new SpringWebMethodParser(httpParser));
            }
        });
    }
}
