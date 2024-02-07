package io.github.astro.virtue.governance.router;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.config.RouterConfig;
import io.github.astro.virtue.config.manager.Virtue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * DefaultRouter
 */
@ServiceProvider(DEFAULT)
public class DefaultRouter implements Router {

    @Override
    public List<URL> route(Invocation invocation, List<URL> urls) {
        URL url = invocation.url();
        Virtue virtue = Virtue.get(url);
        Collection<RouterConfig> routerConfigs = virtue.configManager().routerConfigManager().getManagerMap().values();
        LinkedList<URL> result = new LinkedList<>();
        boolean hadConfig = false;
        for (RouterConfig routerConfig : routerConfigs) {
            Pattern urlPattern = Pattern.compile(routerConfig.urlRegex());
            if (urlPattern.matcher(url.toString()).find()) {
                hadConfig = true;
                for (URL serviceUrl : urls) {
                    Pattern targetPattern = Pattern.compile(routerConfig.matchTargetRegex());
                    if (targetPattern.matcher(serviceUrl.toString()).find()) {
                        result.add(serviceUrl);
                    }
                }
            }
        }
        return hadConfig ? result : urls;
    }
}
