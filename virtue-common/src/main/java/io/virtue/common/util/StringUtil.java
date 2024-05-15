package io.virtue.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for string operations.
 */
public final class StringUtil {

    private StringUtil() {
    }

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

    /**
     * Determines whether the two CharSequences value are equal.
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean equals(CharSequence c1, CharSequence c2) {
        return CharSequence.compare(c1, c2) == 0;
    }

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

    public static Map<CharSequence, CharSequence> getStringMap(String[] params, String separator) {
        if (CollectionUtil.isEmpty(params)) {
            return new HashMap<>();
        }
        return Arrays.stream(params)
                .map(pair -> pair.split(separator))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> keyValue[0].trim(), keyValue -> keyValue[1].trim()));
    }

    /**
     * Replaces the placeholder in the path with the specified value.
     *
     * @param path
     * @param placeholder
     * @param value
     * @return
     */
    public static String replacePlaceholder(String path, String placeholder, String value) {
        return path.replace("{" + placeholder + "}", value);
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
}
