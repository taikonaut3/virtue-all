package io.virtue.core.manager;

import io.virtue.core.config.RouterConfig;
import org.intellij.lang.annotations.Language;

/**
 * RouterConfigManager
 */
public class RouterConfigManager extends AbstractManager<RouterConfig> {
    protected RouterConfigManager(Virtue virtue) {
        super(virtue);
    }

    public void register(@Language("RegExp") String urlRegex, @Language("RegExp") String targetRegex) {
        RouterConfig routerConfig = new RouterConfig(urlRegex).match(targetRegex);
        register(urlRegex, routerConfig);
    }
}
