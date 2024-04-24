package io.virtue.common.util;

import java.util.Objects;

/**
 * Utility class for string operations.
 */
public final class StringUtil {

    /**
     * Determines whether the string is null, that is, null, 0 in length, or contains only whitespace characters.
     *
     * @param str
     * @return
     */
    public static boolean isBlank(CharSequence str) {
        return (str == null || str.isEmpty() || isWhitespace(str));
    }

    /**
     * Determines whether the string is empty, and returns the default value if it is empty.
     *
     * @param target
     * @param defaultValue
     * @return
     */
    public static String isBlankOrDefault(String target, String defaultValue) {
        return isBlank(target) ? defaultValue : target;
    }

    // 获取对象简单类名，如果对象为null，返回"null_object"；否则返回对象类名的简称

    /**
     * Normalize the path to remove the beginning '/', the end '/', and the redundant '/'.
     *
     * @param path
     * @return
     */
    public static String normalizePath(String path) {
        return path.replaceAll("^/+", "")
                .replaceAll("/+$", "")
                .replaceAll("/+", "/");
    }

    /**
     * Get the simple class name of the object.
     * <p>if the object is null, return "null object",
     * otherwise return the abbreviation of the object class name.</p>
     *
     * @param o
     * @return
     */
    public static String simpleClassName(Object o) {
        if (o == null) {
            return "null_object";
        } else {
            return simpleClassName(o.getClass());
        }
    }

    /**
     * Get the short name of the class.
     *
     * @param type
     * @return
     */
    public static String simpleClassName(Class<?> type) {
        Objects.requireNonNull(type);
        return type.getSimpleName();
    }

    /**
     * Determines whether the string contains only whitespace characters.
     *
     * @param str
     * @return
     */
    private static boolean isWhitespace(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    private StringUtil() {
    }
}
