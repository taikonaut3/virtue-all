package io.github.astro.virtue.config;

import io.github.astro.virtue.common.url.URL;

import java.util.function.Function;

/**
 * The basic Invocation implementation class
 */
public class CallInvocation implements Invocation {

    private final CallArgs args;
    private URL url;
    private Function<Invocation, Object> call;

    public CallInvocation(URL url, CallArgs args, Function<Invocation, Object> call) {
        this.url = url;
        this.args = args;
        this.call = call;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public void url(URL url) {
        this.url = url;
    }

    @Override
    public CallArgs callArgs() {
        return args;
    }

    @Override
    public Object invoke() {
        return call.apply(this);
    }

    @Override
    public Invocation turnInvoke(Function<Invocation, Object> function) {
        this.call = function;
        return this;
    }
}
