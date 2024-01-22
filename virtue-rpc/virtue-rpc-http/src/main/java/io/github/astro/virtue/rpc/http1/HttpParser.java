package io.github.astro.virtue.rpc.http1;

import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.rpc.http1.config.HttpMethod;
import io.github.astro.virtue.rpc.http1.config.Param;
import io.github.astro.virtue.rpc.http1.config.PathVariable;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/13 19:30
 */
public class HttpParser implements ProtocolParser {
    @Override
    public CallArgs parseRequestBody(Request request) {
        return null;
    }

    @Override
    public Object parseResponseBody(Response response) {
        return null;
    }

    public Map<String, String> parseHeaders(String[] headers) {
        return getStringMap(headers);
    }

    public Map<String, String> parseParams(String[] params) {
        return getStringMap(params);
    }

    public Map<String, String> parseParams(Parameter[] parameters, Object[] args) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                String key = param.value();
                if (StringUtil.isBlank(key)) {
                    key = parameter.getName();
                }
                map.put(key, String.valueOf(args[i]));
            }
        }
        return map.isEmpty() ? null : map;
    }

    public String parsePathVariables(String path, Parameter[] parameters, Object[] args) {
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
                path = path.replace(replaceKey, String.valueOf(args[i]));
            }
        }
        return path;
    }

    public List<String> pathToList(String path) {
        List<String> list = new ArrayList<>();
        if (StringUtil.isBlank(path)) {
            return list;
        }
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty()) {
                list.add(part);
            }
        }
        return list;
    }

    private Map<String, String> getStringMap(String[] params) {
        if (params == null || params.length == 0) {
            return null;
        }
        return Arrays.stream(params)
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0].trim(),
                        keyValue -> keyValue[1].trim()
                ));
    }

    public Object parseRequestBody(String httpMethod, Invocation invocation) {
        List<String> noBodyMethod = List.of(HttpMethod.GET, HttpMethod.DELETE);
        if (noBodyMethod.contains(httpMethod)) {
            return null;
        }
        CallArgs callArgs = invocation.callArgs();
        return parseRequestBody(callArgs.caller().method().getParameters(), callArgs.args());
    }

    public Object parseRequestBody(Parameter[] parameters, Object[] args) {
        List<Class<? extends Annotation>> executed = List.of(Param.class, PathVariable.class);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation[] annotations = parameter.getAnnotations();
            boolean isMatch = false;
            if (annotations.length == 0) {
                return args[i];
            }
            for (Annotation annotation : annotations) {
                if (!executed.contains(annotation.getClass())) {
                    isMatch = true;
                    break;
                }
            }
            if (isMatch) {
                return args[i];
            }
        }
        return null;
    }
}
