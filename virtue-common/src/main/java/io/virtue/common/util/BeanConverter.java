package io.virtue.common.util;

import io.virtue.common.exception.CommonException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class BeanConverter {

    public static Map<String, String> convertToMap(Object object) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value != null) {
                    map.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                throw new CommonException("Convert To Map Fail", e);
            }
        }
        return map;
    }

    private BeanConverter() {
    }
}
