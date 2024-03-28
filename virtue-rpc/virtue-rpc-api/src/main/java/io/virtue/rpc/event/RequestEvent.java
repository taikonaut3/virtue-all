package io.virtue.rpc.event;

import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.event.AbstractEvent;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.transport.Request;
import io.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Request Event.
 * <p>Wrap the current channel、Invocation and request.</p>
 */
@Getter
@Accessors(fluent = true)
public class RequestEvent extends AbstractEvent<Request> {

    private final Channel channel;

    private final Invocation invocation;

    public RequestEvent(Request request, Channel channel) {
        super(request);
        this.channel = channel;
        URL url = request.url();
        var protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        this.invocation = protocolParser.parseRequestBody(request);
    }

}
