package io.virtue.rpc.h1.support;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.core.support.TransferableInvocation;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * Http Invocation.
 */
public class HttpInvocation extends HttpStructure implements Invocation {

    private final TransferableInvocation invocation;

    public HttpInvocation(Caller<?> caller, Object[] args) {
        invocation = new TransferableInvocation(caller, args);
        if(caller instanceof AbstractHttpCaller<?> httpCaller){
            allArgsConstructor(
                    httpCaller.path(),
                    httpCaller.httpMethod(),
                    httpCaller.requestHeaders(),
                    httpCaller.queryParams(),
                    null
            );
            httpCaller.restInvocationParser().parse(this);
        }
    }

    public HttpInvocation(URL url, Callee<?> callee, Object[] args) {
        invocation = new TransferableInvocation(url, callee, args);
        if (callee instanceof AbstractHttpCallee<?> httpCallee) {
            allArgsConstructor(
                    url.path(),
                    httpCallee.httpMethod(),
                    httpCallee.responseHeaders(),
                    null,
                    null);
        }
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
