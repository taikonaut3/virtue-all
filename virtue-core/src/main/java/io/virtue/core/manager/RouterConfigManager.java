package io.virtue.core.manager;

import io.virtue.core.Virtue;
import io.virtue.core.config.RouterConfig;
import org.intellij.lang.annotations.Language;

/**
 * RouterConfig Manager.
 */
public class RouterConfigManager extends AbstractManager<RouterConfig> {
    protected RouterConfigManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Register a new router config bye call url regex and target url regex.
     *
     * @param urlRegex
     * @param targetRegex
     */
    public void register(@Language("RegExp") String urlRegex, @Language("RegExp") String targetRegex) {
        RouterConfig routerConfig = new RouterConfig(urlRegex).match(targetRegex);
        register(urlRegex, routerConfig);
    }
}
