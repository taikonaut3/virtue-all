package io.github.astro.virtue.config;

import io.github.astro.virtue.config.manager.Virtue;
import org.intellij.lang.annotations.Language;

/**
 * The current config has the rules acting on the Caller.
 *
 * @param <T>
 */
public interface MatchRule<T> {

    /**
     * Add protocol rules.
     *
     * @param virtue
     * @param scope
     * @param regex  use regular expression
     * @return
     */
    T addProtocolRule(Virtue virtue, Scope scope, @Language("RegExp") String... regex);

    /**
     * Add path rules.
     *
     * @param virtue
     * @param scope
     * @param regex  use regular expression
     * @return
     */
    T addPathRule(Virtue virtue, Scope scope, @Language("RegExp") String... regex);

    enum Scope {
        client, server, all
    }
}
