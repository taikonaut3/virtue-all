package io.virtue.rpc.protocol;

import io.virtue.core.Invocation;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

/**
 * ProtocolParser use to parse request and response body.
 */
public interface ProtocolParser {

    /**
     * Parse request body to Invocation.
     *
     * @param request
     * @return
     */
    Invocation parseRequestBody(Request request);

    /**
     * Parse response body to Object.
     *
     * @param response
     * @return
     */
    Object parseResponseBody(Response response);

}
