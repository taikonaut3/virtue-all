package io.virtue.rpc.http1_1.config;

import io.virtue.config.CallArgs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface MethodParser<A extends Annotation> {

    Class<A> support();

    HttpRequestWrapper parse(Method method);

    Map<String, String> parseParams(CallArgs args, HttpRequestWrapper wrapper);

    String parsePathVariables(CallArgs args, HttpRequestWrapper wrapper);

    Object parseRequestBody(CallArgs args, HttpRequestWrapper wrapper);

}
