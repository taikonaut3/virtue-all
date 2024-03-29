package io.virtue.registry;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.SystemInfo;
import io.virtue.core.Virtue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract RegistryService.
 */
public abstract class AbstractRegistryService implements RegistryService {

    protected final Map<String, List<String>> discoverHealthServices = new ConcurrentHashMap<>();

    protected URL registryUrl;

    protected boolean enableHealthCheck;

    protected AbstractRegistryService(URL url) {
        registryUrl = url;
        enableHealthCheck = url.getBooleanParam(Key.ENABLE_HEALTH_CHECK, true);
        connect(url);
    }

    @Override
    public List<URL> discover(URL url) {
        String serviceName = serviceName(url);
        List<URL> urls;
        // 1、Determine whether a service is available in the cache
        List<String> serverUrls = discoverHealthServices.get(serviceName);
        if (serverUrls == null || serverUrls.isEmpty()) {
            boolean noSubscribe = serverUrls == null;
            // 2、Available services are found in the registry
            urls = doDiscover(url);
            serverUrls = urls.stream().map(URL::toString).toList();
            // 3、Changes to Subscription Services
            if (noSubscribe) subscribeService(url);
            discoverHealthServices.put(serviceName, serverUrls);
        } else {
            urls = serverUrls.stream()
                    .map(URL::valueOf)
                    .filter(serverUrl -> serverUrl.protocol().equalsIgnoreCase(url.protocol()))
                    .toList();
        }
        return urls;
    }

    protected Map<String, String> metaInfo(URL url) {
        Virtue virtue = Virtue.get(url);
        Map<String, String> systemInfo = new SystemInfo(virtue).toMap();
        HashMap<String, String> registryMeta = new HashMap<>(systemInfo);
        registryMeta.put(Key.PROTOCOL, url.protocol());
        registryMeta.put(Key.WEIGHT, String.valueOf(virtue.configManager().applicationConfig().weight()));
        return registryMeta;
    }

    protected String instanceId(URL url) {
        String application = url.getParam(Key.APPLICATION);
        return application + "-" + url.protocol() + ":" + url.port();
    }

    protected String serviceName(URL url) {
        String applicationName = Virtue.get(url).applicationName();
        applicationName = StringUtil.isBlankOrDefault(applicationName, this.getClass().getModule().getName());
        return url.getParam(Key.APPLICATION, applicationName);
    }

    protected abstract void subscribeService(URL url);

    protected abstract List<URL> doDiscover(URL url);

}
