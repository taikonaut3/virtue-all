package io.virtue.core;

import io.virtue.common.url.URL;

/**
 * Create an Invocation based on the protocol, and the Invocation Factory is bound to the protocol.
 */
public interface InvocationFactory {

    /**
     * Created when requested by the client.
     *
     * @param caller
     * @param args
     * @return
     */
    Invocation createInvocation(Caller<?> caller, Object[] args);

    /**
     * Created after the server deserializes.
     *
     * @param url
     * @param callee
     * @param args
     * @return
     */
    Invocation createInvocation(URL url, Callee<?> callee, Object[] args);
}
