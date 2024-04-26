package io.virtue.rpc.h2;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.rpc.h1.support.HttpStructure;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * Http2 Invocation.
 */
public class Http2Invocation extends HttpStructure implements Invocation {

    private final TransferableInvocation invocation;

    public Http2Invocation(Caller<?> caller, Object[] args) {
        invocation = new TransferableInvocation(caller, args);
        Http2Caller http2Caller = (Http2Caller) caller;
        allArgsConstructor(
                http2Caller.path(),
                http2Caller.httpMethod(),
                http2Caller.requestHeaders(),
                http2Caller.queryParams(),
                null
        );
        http2Caller.restInvocationParser().parse(this);
    }

    public Http2Invocation(URL url, Callee<?> callee, Object[] args) {
        invocation = new TransferableInvocation(url, callee, args);
        Http2Callee http2Callee = (Http2Callee) callee;
        allArgsConstructor(
                url.path(),
                http2Callee.httpMethod(),
                http2Callee.responseHeaders(),
                null,
                null);
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
