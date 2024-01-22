package io.github.astro.rpc.virtue;

import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.rpc.virtue.envelope.VirtueRequest;
import io.github.astro.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/3 17:09
 */
public class VirtueProtocolParser implements ProtocolParser {

    @Override
    public CallArgs parseRequestBody(Request request) {
        VirtueRequest virtueRequest = (VirtueRequest) request.message();
        return (CallArgs) virtueRequest.getBody();
    }

    @Override
    public Object parseResponseBody(Response response) {
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        return virtueResponse.getBody();
    }
}
