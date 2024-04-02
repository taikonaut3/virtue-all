package io.virtue.rpc.event;

import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.event.AbstractEvent;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.transport.Response;
import lombok.Getter;

/**
 * ResponseEvent.
 */
@Getter
public class ResponseEvent extends AbstractEvent<Response> {

    private final Object body;

    public ResponseEvent(Response response) {
        super(response);
        URL url = response.url();
        var protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        this.body = protocolParser.parseResponseBody(response);
    }

}
