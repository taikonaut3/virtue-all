package io.virtue.rpc.protocol;

import io.virtue.common.exception.ResourceException;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract ProtocolParser.
 *
 * @param <Req>
 * @param <Resp>
 */
public abstract class AbstractProtocolParser<Req, Resp> implements ProtocolParser {

    @SuppressWarnings("unchecked")
    @Override
    public Invocation parseRequestBody(Request request) {
        URL url = request.url();
        Virtue virtue = Virtue.get(url);
        Callee<?> callee = virtue.configManager().remoteServiceManager().getCallee(url);
        if (callee == null) {
            throw new ResourceException("Can't find  ProviderCaller[" + url.path() + "]");
        }
        try {
            Req message = (Req) request.message();
            return parseToInvocation(request, message, callee);
        } catch (Exception e) {
            throw new UnsupportedOperationException("unsupported parse request message type: " + simpleClassName(request.message()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object parseResponseBody(Response response) {
        long id = response.id();
        RpcFuture future = RpcFuture.getFuture(id);
        if (future != null) {
            Caller<?> caller = (Caller<?>) future.invocation().invoker();
            try {
                Resp message = (Resp) response.message();
                return parseToReturnObject(response, message, caller);
            } catch (Exception e) {
                throw new UnsupportedOperationException("unsupported parse response message type: " + simpleClassName(response.message()));
            }
        }
        return null;
    }

    protected abstract Invocation parseToInvocation(Request request, Req message, Callee<?> callee);

    protected abstract Object parseToReturnObject(Response response, Resp message, Caller<?> caller);
}
