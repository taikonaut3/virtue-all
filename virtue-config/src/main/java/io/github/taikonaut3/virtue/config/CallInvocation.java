package io.github.taikonaut3.virtue.config;

import io.github.taikonaut3.virtue.common.url.URL;

import java.util.function.Supplier;

/**
 * The basic Invocation implementation class
 */
public class CallInvocation implements Invocation {

    private final CallArgs args;

    private URL url;

    private Supplier<Object> invoke;

    public CallInvocation(URL url, CallArgs args, Supplier<Object> invoke) {
        this.url = url;
        this.args = args;
        this.invoke = invoke;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public CallArgs callArgs() {
        return args;
    }

    @Override
    public Object invoke() {
        if (invoke != null) {
            return invoke.get();
        }
        return null;
    }

    @Override
    public Invocation revise(URL url) {
        if (url != null) this.url = url;
        return this;
    }

    @Override
    public Invocation revise(Supplier<Object> invoke) {
        this.invoke = invoke;
        return this;
    }

    @Override
    public Invocation revise(URL url, Supplier<Object> invoke) {
        revise(url).revise(invoke);
        return this;
    }
}
