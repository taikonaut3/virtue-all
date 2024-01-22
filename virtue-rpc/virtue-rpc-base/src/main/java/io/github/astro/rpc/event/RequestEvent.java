package io.github.astro.rpc.event;

import io.github.astro.rpc.protocol.Protocol;
import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.AbstractEvent;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.channel.Channel;
import lombok.Getter;

@Getter
public class RequestEvent extends AbstractEvent<Request> {

    private final Channel channel;

    private final Object body;

    public RequestEvent(Request request, Channel channel) {
        super(request);
        this.channel = channel;
        URL url = request.url();
        Protocol protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.getParser(url);
        this.body = protocolParser.parseRequestBody(request);
    }

}
