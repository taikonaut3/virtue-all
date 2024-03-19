package io.virtue.governance.router;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.core.config.RouterConfig;
import io.virtue.core.manager.Virtue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * DefaultRouter
 */
@ServiceProvider(DEFAULT)
public class DefaultRouter implements Router {

    @Override
    public List<URL> route(Invocation invocation, List<URL> urls) {
        URL url = invocation.url();
        // filter by group and version
        String group = url.getParameter(Key.GROUP);
        String version = url.getParameter(Key.VERSION);
        if (!StringUtil.isBlank(group)) {
            urls = urls.stream().filter(item -> Objects.equals(item.getParameter(Key.GROUP), group)).collect(Collectors.toList());
        }
        if(!StringUtil.isBlank(version)){
            urls = urls.stream().filter(item -> Objects.equals(item.getParameter(Key.VIRTUE), version)).collect(Collectors.toList());
        }
        // filter by router rule
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
