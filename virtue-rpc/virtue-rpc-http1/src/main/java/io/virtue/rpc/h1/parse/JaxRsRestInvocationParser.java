package io.virtue.rpc.h1.parse;

import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.rpc.h1.support.HttpStructure;
import io.virtue.rpc.h1.support.HttpUtil;
import io.virtue.serialization.Serializer;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.h1.HttpRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

import static io.virtue.common.constant.Components.RestParser.JAX_RS;
import static io.virtue.common.util.StringUtil.replacePlaceholder;

/**
 * Support JAX-RS annotation.
 */
@Extension(JAX_RS)
public class JaxRsRestInvocationParser implements RestInvocationParser {
    @Override
    public void parse(Invocation invocation) throws RpcException {
        if (invocation instanceof HttpStructure httpStructure) {
            Object[] args = invocation.args();
            Parameter[] parameters = invocation.invoker().method().getParameters();
            for (int i = 0; i < parameters.length; i++) {
                try {
                    parseJaxAnnotation(parameters[i], args[i], httpStructure);
                } catch (Exception e) {
                    throw RpcException.unwrap(e);
                }
            }
            invocation.url().replacePaths(URL.pathToList(httpStructure.path()));
        }
    }

    @Override
    public Object[] parse(HttpRequest httpRequest, Callee<?> callee) throws RpcException {
        Parameter[] parameters = callee.method().getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            try {
                Parameter parameter = parameters[i];
                Object arg = parseForJaxAnnotation(parameter, callee, httpRequest);
                args[i] = arg;
            } catch (Exception e) {
                throw RpcException.unwrap(e);
            }
        }
        return args;
    }

    private Object parseForJaxAnnotation(AnnotatedElement element, Callee<?> callee, HttpRequest request) throws Exception {
        if (element.isAnnotationPresent(Body.class) && element instanceof Parameter parameter) {
            String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE).toString();
            Serializer serializer = HttpUtil.getSerializer(contentType);
            return serializer.deserialize(request.data(), parameter.getParameterizedType());
        }
        if (element.isAnnotationPresent(PathParam.class)) {
            PathParam pathParam = element.getAnnotation(PathParam.class);
            int index = callee.pathList().indexOf("{" + pathParam.value() + "}");
            if (index != -1) {
                return URL.pathToList(request.url().path()).get(index);
            }
        } else if (element.isAnnotationPresent(QueryParam.class)) {
            QueryParam queryParam = element.getAnnotation(QueryParam.class);
            return request.url().getParam(queryParam.value());
        } else if (element.isAnnotationPresent(HeaderParam.class)) {
            HeaderParam headerParam = element.getAnnotation(HeaderParam.class);
            return String.valueOf(request.headers().get(headerParam.value()));
        } else if (element.isAnnotationPresent(BeanParam.class)) {
            if (element instanceof Parameter parameter) {
                Class<?> classType = parameter.getType();
                Object instance = ReflectionUtil.createInstance(classType);
                List<Field> fields = ReflectionUtil.getAllFields(classType);
                for (Field field : fields) {
                    Object result = parseForJaxAnnotation(field, callee, request);
                    result = ReflectionUtil.convertValue(field.getType(), result);
                    if (result != null) {
                        field.setAccessible(true);
                        field.set(instance, result);
                    }
                }
                return instance;
            }
        }
        return null;
    }

    private void parseJaxAnnotation(AnnotatedElement element, Object arg, HttpStructure httpStructure) throws Exception {
        if (element.isAnnotationPresent(Body.class)) {
            httpStructure.body(arg);
            return;
        }
        if (element.isAnnotationPresent(PathParam.class)) {
            PathParam pathParam = element.getAnnotation(PathParam.class);
            String path = httpStructure.path();
            path = replacePlaceholder(path, pathParam.value(), String.valueOf(arg));
            httpStructure.path(path);
        }
        if (element.isAnnotationPresent(QueryParam.class)) {
            QueryParam queryParam = element.getAnnotation(QueryParam.class);
            httpStructure.addParam(queryParam.value(), String.valueOf(arg));
        }
        if (element.isAnnotationPresent(HeaderParam.class)) {
            HeaderParam headerParam = element.getAnnotation(HeaderParam.class);
            httpStructure.addHeader(headerParam.value(), String.valueOf(arg));
        }
        if (element.isAnnotationPresent(BeanParam.class)) {
            List<Field> fields = ReflectionUtil.getAllFields(arg.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(arg);
                parseJaxAnnotation(field, value, httpStructure);
            }
        }
    }
}
