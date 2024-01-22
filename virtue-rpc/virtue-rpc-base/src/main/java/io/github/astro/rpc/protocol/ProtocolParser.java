package io.github.astro.rpc.protocol;

import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/3 17:03
 */
public interface ProtocolParser {

    CallArgs parseRequestBody(Request request);

    Object parseResponseBody(Response response);

}
