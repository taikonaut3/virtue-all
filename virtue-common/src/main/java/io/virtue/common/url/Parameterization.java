package io.virtue.common.url;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.common.util.StringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementing this interface allows converting all fields.
 * <p>Including those annotated with {@link Parameter}, of the object (including All fields from parent classes),
 * * Into a map using the {@link Parameterization#parameterization()} method.</p>
 */
public interface Parameterization {

    /**
     * Map the fields annotated with {@link Parameter} to a map.
     *
     * @return map
     */
    default Map<String, String> parameterization() {
        HashMap<String, String> map = new LinkedHashMap<>();
        List<Field> fields = ReflectionUtil.getAllFields(this.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Parameter.class)) {
                String key = field.getAnnotation(Parameter.class).value();
                String value;
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(this);
                    if (fieldValue != null) {
                        value = String.valueOf(fieldValue);
                        if (!StringUtil.isBlank(value)) {
                            map.put(key, value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw RpcException.unwrap(e);
                }
            }
        }
        return map;
    }

}
