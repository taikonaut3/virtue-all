package io.virtue.rpc.h2;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.rpc.h2.envelope.HttpEnvelope;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import static io.virtue.transport.util.TransportUtil.getHttpMethod;

/**
 * Http2 Invocation.
 */
public class Http2Invocation extends HttpEnvelope implements Invocation {

    private final TransferableInvocation invocation;

    public Http2Invocation(Caller<?> caller, Object[] args) {
        invocation = new TransferableInvocation(caller, args);
        Http2Wrapper wrapper = ((Http2Caller) caller).wrapper();
        allArgsConstructor(wrapper.path(), wrapper.httpMethod(), wrapper.headers(), wrapper.params(), HttpUtil.findBody(invocation));
    }

    public Http2Invocation(URL url, Callee<?> callee, Object[] args) {
        invocation = new TransferableInvocation(url, callee, args);
        Http2Wrapper wrapper = ((Http2Callee) callee).wrapper();
        allArgsConstructor(url.path(), getHttpMethod(url), wrapper.headers(), null, null);
    }

    @Override
    public URL url() {
        return invocation.url();
    }

    @Override
    public Object[] args() {
        return invocation.args();
    }

    @Override
    public Type returnType() {
        return invocation.returnType();
    }

    @Override
    public Type[] parameterTypes() {
        return invocation.parameterTypes();
    }

    @Override
    public Invoker<?> invoker() {
        return invocation.invoker();
    }

    @Override
    public Object invoke() {
        return invocation.invoke();
    }

    @Override
    public Invocation revise(Supplier<Object> invoke) {
        return invocation.revise(invoke);
    }
}
