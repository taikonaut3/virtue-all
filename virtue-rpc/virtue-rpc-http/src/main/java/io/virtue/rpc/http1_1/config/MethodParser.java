package io.virtue.rpc.http1_1.config;

import io.virtue.core.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface MethodParser<A extends Annotation> {

    Class<A> support();

    HttpRequestWrapper parse(Method method);

    Map<String, String> parseParams(Invocation invocation, HttpRequestWrapper wrapper);

    String parsePathVariables(Invocation invocation, HttpRequestWrapper wrapper);

    Object parseRequestBody(Invocation invocation, HttpRequestWrapper wrapper);

}
