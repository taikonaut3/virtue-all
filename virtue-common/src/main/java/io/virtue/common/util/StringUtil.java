package io.virtue.common.util;

import java.util.Objects;

public interface StringUtil {

    static boolean isBlank(String str) {
        return (str == null || str.isEmpty() || isWhitespace(str));
    }

    static String isBlankOrDefault(String target, String defaultValue) {
        return isBlank(target) ? defaultValue : target;
    }

    static String normalizePath(String path) {
        return path.replaceAll("^/+", "")
                .replaceAll("/+$", "")
                .replaceAll("/+", "/");
    }

    static String simpleClassName(Object o) {
        if (o == null) {
            return "null_object";
        } else {
            return simpleClassName(o.getClass());
        }
    }

    static String simpleClassName(Class<?> type) {
        Objects.requireNonNull(type);
        return type.getSimpleName();
    }

    private static boolean isWhitespace(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
