package io.virtue.proxy;

/**
 * Used reflect the original method.
 *
 * @param <R> The return type of the method.
 */
@FunctionalInterface
public interface SuperInvoker<R> {

    /**
     * Invokes the original method.
     *
     * @return The result of the method invocation.
     * @throws Throwable if an error occurs during the method invocation.
     */
    R invoke() throws Throwable;
}

