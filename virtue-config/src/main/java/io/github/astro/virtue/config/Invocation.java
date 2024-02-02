package io.github.astro.virtue.config;

import io.github.astro.virtue.common.url.URL;

import java.util.function.Function;

/**
 * Local Invoker, used to flow invoke for each component
 */
public interface Invocation {

    /**
     * Get the URL of the invocation
     */
    URL url();

    /**
     * Set the URL of the invocation
     *
     * @param url newUrl
     */
    void url(URL url);

    /**
     * Get CallArgs of the invocation
     */
    CallArgs callArgs();

    /**
     * Invoke the invocation and return the result.
     */
    Object invoke();

    /**
     * Turn the invocation and execute the specified function
     *
     * @param function
     */
    Invocation turnInvoke(Function<Invocation, Object> function);

    static Invocation create(URL url, CallArgs args, Function<Invocation, Object> function) {
        return new CallInvocation(url, args, function);
    }

}
