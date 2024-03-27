package io.virtue.common.util;

import java.util.Objects;

/**
 * Assert tool, which provides some common assertions methods.
 */
public final class AssertUtil {

    /**
     * Determine if a condition is valid, and throw a AssertionError exception when not.
     *
     * @param condition
     */
    public static void condition(boolean condition) {
        condition(condition, null);
    }

    /**
     * Determine if a condition is valid, and throw a AssertionError exception when not.
     *
     * @param condition
     * @param message
     */
    public static void condition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Determine if an object is not null, and throw a NullPointerException exception when it is.
     *
     * @param object
     */
    public static void notNull(Object object) {
        notNull(object, null);
    }

    /**
     * Determine if an object is not null, and throw a NullPointerException exception when it is.
     *
     * @param objects
     */
    public static void notNull(Object... objects) {
        for (Object object : objects) {
            notNull(object);
        }
    }

    /**
     * Determine if an object is not null, and throw a NullPointerException exception when it is.
     *
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            if (StringUtil.isBlank(message))
                message = "The object can't be null";
            throw new NullPointerException(message);
        }
    }

    /**
     * Determine if a string is not empty.
     *
     * @param str
     * @param message
     */
    public static void notBlank(String str, String message) {
        condition(!StringUtil.isBlank(str), message);
    }

    /**
     * Determine whether the two objects are equal.
     *
     * @param expected
     * @param actual
     */
    public static void equals(Object expected, Object actual) {
        equals(expected, actual, null);
    }

    /**
     * Determine whether the two objects are equal.
     *
     * @param expected
     * @param actual
     * @param message
     */
    public static void equals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message);
        }
    }

    private AssertUtil() {
    }

}
