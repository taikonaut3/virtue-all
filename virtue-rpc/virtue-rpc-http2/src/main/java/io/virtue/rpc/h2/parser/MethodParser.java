package io.virtue.rpc.h2.parser;

import io.virtue.core.Invocation;

/**
 * Parse Http method.
 */
public interface MethodParser {

    void parseMethod(Invocation invocation);
}
