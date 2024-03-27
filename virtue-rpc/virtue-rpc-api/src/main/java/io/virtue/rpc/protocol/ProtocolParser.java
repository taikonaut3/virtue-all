package io.virtue.rpc.protocol;

import io.virtue.core.Invocation;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

public interface ProtocolParser {

    Invocation parseRequestBody(Request request);

    Object parseResponseBody(Response response);

}
