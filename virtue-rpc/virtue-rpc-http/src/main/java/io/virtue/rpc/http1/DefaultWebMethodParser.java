package io.virtue.rpc.http1;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RequestBody;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.rpc.http1_1.config.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Support Spring Web API
 */
public class DefaultWebMethodParser implements MethodParser<HttpCall> {

    private final HttpParser httpParser;

    public DefaultWebMethodParser(HttpParser httpParser) {
        this.httpParser = httpParser;
    }

    @Override
    public Class<HttpCall> support() {
        return HttpCall.class;
    }

    @Override
    public HttpRequestWrapper parse(Method method) {
        HttpCall httpCall = method.getAnnotation(HttpCall.class);
        if (httpCall == null) {
            return null;
        }
        HttpRequestWrapper wrapper = new HttpRequestWrapper(method);
        wrapper.httpMethod(StringUtil.isBlank(httpCall.method()) ? HttpMethod.GET : httpCall.method());
        wrapper.path(httpCall.path());
        wrapper.headers(httpParser.parseHeaders(httpCall.headers()));
        wrapper.params(httpParser.parseParams(httpCall.params()));
        return wrapper;
    }

    @Override
    public Map<String, String> parseParams(Invocation invocation, HttpRequestWrapper wrapper) {
        Parameter[] parameters = invocation.invoker().method().getParameters();
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                String key = param.value();
                if (StringUtil.isBlank(key)) {
                    key = parameter.getName();
                }
                map.put(key, String.valueOf(invocation.args()[i]));
            }
        }
        wrapper.params().putAll(map);
        return wrapper.params();
    }

    @Override
    public String parsePathVariables(Invocation invocation, HttpRequestWrapper wrapper) {
        String path = wrapper.path();
        Parameter[] parameters = invocation.invoker().method().getParameters();
        if (StringUtil.isBlank(path)) {
            return path;
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String key = pathVariable.value();
                if (StringUtil.isBlank(key)) {
                    key = parameter.getName();
                }
                String replaceKey = "{" + key + "}";
                path = path.replace(replaceKey, String.valueOf(invocation.args()[i]));
            }
        }
        return path;
    }

    @Override
    public Object parseRequestBody(Invocation invocation, HttpRequestWrapper wrapper) {
        List<Class<? extends Annotation>> executed = List.of(Param.class, PathVariable.class);
        Parameter[] parameters = invocation.invoker().method().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation[] annotations = parameter.getAnnotations();
            boolean isMatch = false;
            if (annotations.length == 0) {
                wrapper.headers().put(HttpHeaderNames.CONTENT_TYPE.toString(), ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
                return invocation.args()[i];
            }
            List<? extends Class<? extends Annotation>> annotationTypes = Arrays.stream(annotations).map(Annotation::annotationType).toList();
            if (annotationTypes.contains(RequestBody.class)) {
                wrapper.headers().put(HttpHeaderNames.CONTENT_TYPE.toString(), ContentType.APPLICATION_JSON);
                return invocation.args()[i];
            }
            for (Annotation annotation : annotations) {
                if (!executed.contains(annotation.annotationType())) {
                    isMatch = true;
                    break;
                }
            }
            if (isMatch) {
                return invocation.args()[i];
            }
        }
        return null;
    }
}
