package io.virtue.rpc.h1.parse;

import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.transport.http.h1.HttpRequest;

import static io.virtue.common.constant.Components.RestParser.JAX_RS;

/**
 * Parse Http method.
 */
@Extensible(JAX_RS)
public interface RestInvocationParser {

    /**
     * Parse http's dynamic parameters.
     *
     * @param invocation
     * @throws RpcException
     */
    void parse(Invocation invocation) throws RpcException;

    /**
     * Convert method parameters from http request.
     *
     * @param httpRequest
     * @param callee
     * @return
     * @throws RpcException
     */
    Object[] parse(HttpRequest httpRequest, Callee<?> callee) throws RpcException;
}
