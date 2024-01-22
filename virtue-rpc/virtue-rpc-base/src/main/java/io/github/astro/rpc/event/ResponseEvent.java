package io.github.astro.rpc.event;

import io.github.astro.rpc.protocol.Protocol;
import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.AbstractEvent;
import io.github.astro.virtue.transport.Response;
import lombok.Getter;

@Getter
public class ResponseEvent extends AbstractEvent<Response> {

    private final Object body;

    public ResponseEvent(Response response) {
        super(response);
        URL url = response.url();
        Protocol protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.getParser(url);
        this.body = protocolParser.parseResponseBody(response);
    }

}
