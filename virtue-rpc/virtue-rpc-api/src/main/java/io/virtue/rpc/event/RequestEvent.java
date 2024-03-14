package io.virtue.rpc.event;

import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.event.AbstractEvent;
import io.virtue.transport.Request;
import io.virtue.transport.channel.Channel;
import lombok.Getter;

@Getter
public class RequestEvent extends AbstractEvent<Request> {

    private final Channel channel;

    private final Object body;

    public RequestEvent(Request request, Channel channel) {
        super(request);
        this.channel = channel;
        URL url = request.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        this.body = protocolParser.parseRequestBody(request);
    }

}
