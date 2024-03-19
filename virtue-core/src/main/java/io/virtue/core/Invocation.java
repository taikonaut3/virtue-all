package io.virtue.core;

import io.virtue.common.url.URL;
import io.virtue.core.support.CallInvocation;

import java.util.function.Supplier;

/**
 * Encapsulate the url, callArgs, and invocation behavior.
 */
public interface Invocation {

    /**
     * Get the url of the invocation.
     *
     * @return current Invocation url
     */
    URL url();

    /**
     * Get CallArgs of the invocation.
     *
     * @return current Invocation callArgs
     */
    CallArgs callArgs();

    /**
     * Invoke the invocation and return the result.
     *
     * @return invocation behavior (could be null)
     */
    Object invoke();

    /**
     * Revise current url.
     *
     * @param url
     * @return current instance
     */
    Invocation revise(URL url);

    /**
     * Revise current invocation behavior.
     *
     * @param invoke
     * @return current instance
     */
    Invocation revise(Supplier<Object> invoke);

    /**
     * Revise current url and invocation behavior.
     *
     * @param url
     * @param invoke
     * @return callInvocation instance
     */
    Invocation revise(URL url, Supplier<Object> invoke);

    /**
     * Create invocation instance use {@link CallInvocation}.
     *
     * @param url
     * @param args
     * @param invoke
     * @return callInvocation instance
     */
    static Invocation create(URL url, CallArgs args, Supplier<Object> invoke) {
        return new CallInvocation(url, args, invoke);
    }

    /**
     * Create invocation instance use {@link CallInvocation}.
     *
     * @param url
     * @param args
     * @return callInvocation instance
     */
    static Invocation create(URL url, CallArgs args) {
        return new CallInvocation(url, args, null);
    }

}
