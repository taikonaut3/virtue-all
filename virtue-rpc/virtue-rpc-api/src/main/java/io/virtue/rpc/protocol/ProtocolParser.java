package io.virtue.rpc.protocol;

import io.virtue.config.CallArgs;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

public interface ProtocolParser {

    CallArgs parseRequestBody(Request request);

    Object parseResponseBody(Response response);

}
