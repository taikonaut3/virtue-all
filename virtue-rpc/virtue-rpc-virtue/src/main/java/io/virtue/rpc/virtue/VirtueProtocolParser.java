package io.virtue.rpc.virtue;

import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

/**
 * VirtueProtocolParser
 */
public class VirtueProtocolParser implements ProtocolParser {

    @Override
    public Invocation parseRequestBody(Request request) {
        VirtueRequest virtueRequest = (VirtueRequest) request.message();
        return (Invocation) virtueRequest.body();
    }

    @Override
    public Object parseResponseBody(Response response) {
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        return virtueResponse.body();
    }
}
