package io.virtue.governance.router;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.core.config.RouterConfig;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Default Router.
 */
@Extension(DEFAULT)
public class DefaultRouter implements Router {

    @Override
    public List<URL> route(Invocation invocation, List<URL> urls) {
        URL url = invocation.url();
        // filter by group
        String group = url.getParam(Key.GROUP);
        if (!StringUtil.isBlank(group)) {
            urls = urls.stream().filter(item -> Objects.equals(item.getParam(Key.GROUP), group)).collect(Collectors.toList());
        }
        // filter by router rule
        Virtue virtue = Virtue.ofClient(url);
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
