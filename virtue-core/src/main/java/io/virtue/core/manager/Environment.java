package io.virtue.core.manager;

import io.virtue.common.extension.StringAccessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/20 16:24
 */
public class Environment extends StringAccessor<Environment> {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * Try replacing ${} in the value with the key of the current environment.
     *
     * @param input
     * @return
     */
    public String replace(String input) {
        Matcher matcher = PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = get(key);
            if (replacement == null) {
                replacement = "null";
            }
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
