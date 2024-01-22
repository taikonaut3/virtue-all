package io.github.astro.virtue.config.util;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.ClientCaller;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 生成工具类
 */
public interface GenerateUtil {

    static String generateKey(URL url) {
        return url.path();
    }

    static String generateKey(CallArgs callArgs) {
        ClientCaller<?> caller = (ClientCaller<?>) callArgs.caller();
        return caller.url().path();
    }

    static String generateKey(Method method) {
        StringBuilder builder = new StringBuilder();
        String name = method.getName();
        builder.append(name);
        Type[] types = method.getGenericParameterTypes();
        if (types.length > 0) {
            builder.append("(");
            for (int i = 0; i < types.length; i++) {
                builder.append(types[i].getTypeName());
                if (i != types.length - 1) {
                    builder.append(",");
                }
            }
            builder.append(")");
        }

        return builder.toString();
    }

    static String generateMetaProtocolKey(String protocol) {
        return Key.PROTOCOL_PREFIX + protocol;
    }

}
