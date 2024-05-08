package io.virtue.core;

/**
 * Match scope.
 */
public enum MatchScope {

    /**
     * The configuration takes effect only on the caller.
     */
    CALLER,

    /**
     * The configuration takes effect only on the callee.
     */
    CALLEE,

    /**
     * The configuration takes effect for both the caller and the callee.
     */
    INVOKER
}
