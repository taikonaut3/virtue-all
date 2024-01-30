package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.rpc.http1_1.config.Param;
import io.github.astro.virtue.rpc.http1_1.config.PathVariable;
import io.github.astro.virtue.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.serialization.Serializer;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.RpcFuture;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;

/**
 * HttpParser
 */
public class HttpParser implements ProtocolParser {

    private final List<Annotation> paramAnnotations = new LinkedList<>();

    private final List<Annotation> pathVariableAnnotations = new LinkedList<>();

    @Override
    public CallArgs parseRequestBody(Request request) {
        return null;
    }

    @Override
    public Object parseResponseBody(Response response) {
        FullHttpResponse httpResponse = (FullHttpResponse) response.message();
        ByteBuf byteBuf = httpResponse.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Serializer serializer = ExtensionLoader.loadService(Serializer.class, JSON);
        RpcFuture future = RpcFuture.getFuture(String.valueOf(response.id()));
        Caller<?> caller = future.callArgs().caller();
        Type type = caller.returnType();
        Object object = serializer.deserialize(bytes, caller.returnClass());
        return serializer.convert(object, type);
    }

    public Map<String, String> parseHeaders(String[] headers) {
        return getStringMap(headers);
    }

    public List<String> parsePaths(HttpClientCaller caller, CallArgs args) {
        return pathToList(parsePathVariables(caller.path(), caller.method().getParameters(), args.args()));
    }

    public Map<String, String> parseParams(HttpClientCaller caller, CallArgs args) {
        Parameter[] parameters = caller.method().getParameters();
        Map<String, String> initParams = caller.params();
        Map<String, String> argParams = parseParams(parameters, args.args());
        if (initParams != null) {
            argParams.putAll(initParams);
        }
        return argParams;
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
        return map;
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
                if (!executed.contains(annotation.annotationType())) {
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
