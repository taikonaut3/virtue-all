package io.github.astro.virtue.rpc.protocol;

import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;

public interface ProtocolParser {

    CallArgs parseRequestBody(Request request);

    Object parseResponseBody(Response response);

}
