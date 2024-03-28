package io.virtue.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date operations.
 */
public final class DateUtil {

    // The format of the datetime
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Compact datetime format
    public static final String COMPACT_FORMAT = "yyyyMMddHHmmss";

    /**
     * Format the datetime string based on the given format string and datetime object.
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * Based on the given format string and datetime string, the datetime object is resolved.
     *
     * @param str
     * @param pattern
     * @return
     */
    public static LocalDateTime parse(String str, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(str, formatter);
    }

    /**
     * Format the datetime string based on the default datetime format string and datetime object.
     *
     * @param dateTime
     * @return
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DATETIME_FORMAT);
    }

    private DateUtil() {
    }

}