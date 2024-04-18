package io.virtue.rpc.protocol;

import io.virtue.core.Invocation;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

/**
 * ProtocolParser use to parse request and response body.
 */
public interface ProtocolParser {

    /**
     * Parse request to Invocation.
     *
     * @param request
     * @return
     */
    Invocation parseOfRequest(Request request);

    /**
     * Parse response to return value.
     *
     * @param response
     * @return
     */
    Object parseOfResponse(Response response);

}
