package io.virtue.rpc.protocol;

import io.virtue.common.exception.ResourceException;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract ProtocolParser.
 *
 * @param <Req>
 * @param <Resp>
 */
public abstract class AbstractProtocolParser<Req, Resp> implements ProtocolParser {

    protected Protocol<Req, Resp> protocol;

    public void protocol(Protocol<Req, Resp> protocol) {
        this.protocol = protocol;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Invocation parseRequestBody(Request request) {
        URL url = request.url();
        Virtue virtue = Virtue.ofServer(url);
        Callee<?> callee = virtue.configManager().remoteServiceManager().getCallee(url);
        if (callee == null) {
            throw new ResourceException("Can't find  ProviderCaller[" + url.path() + "]");
        }
        Req message;
        try {
            message = (Req) request.message();
        } catch (Exception e) {
            throw new UnsupportedOperationException(simpleClassName(this) + " unsupported parse request message type: " + simpleClassName(request.message()));
        }
        Object[] args = parseToInvokeArgs(request, message, callee);
        return protocol.invokerFactory().createInvocation(request.url(), callee, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object parseResponseBody(Response response) {
        long id = response.id();
        RpcFuture future = RpcFuture.getFuture(id);
        if (future != null) {
            Caller<?> caller = (Caller<?>) future.invocation().invoker();
            Resp message;
            try {
                message = (Resp) response.message();
            } catch (Exception e) {
                throw new UnsupportedOperationException("unsupported parse response message type: " + simpleClassName(response.message()));
            }
            return parseToReturnValue(response, message, caller);
        }
        return null;
    }

    protected abstract Object[] parseToInvokeArgs(Request request, Req message, Callee<?> callee);

    protected abstract Object parseToReturnValue(Response response, Resp message, Caller<?> caller);
}
