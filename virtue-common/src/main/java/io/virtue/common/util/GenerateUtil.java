package io.virtue.common.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Utility class for generate operations.
 */
public final class GenerateUtil {

    /**
     * The method that generates the method key.
     *
     * @param method
     * @return
     */
    public static String generateKey(Method method) {
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

    /**
     * The method that generates the invoker map.
     *
     * @param protocol
     * @param path
     * @return
     */
    public static String generateInvokerMapping(String protocol, String path) {
        return protocol + ":" + path;
    }
}
