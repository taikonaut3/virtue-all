package io.github.astro.virtue.registry;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Virtue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegistry implements Registry {

    protected final Map<String, List<String>> discoverHealthServices = new ConcurrentHashMap<>();

    protected URL registryUrl;

    protected boolean enableHealthCheck;

    protected AbstractRegistry(URL url) {
        registryUrl = url;
        enableHealthCheck = url.getBooleanParameter(Key.ENABLE_HEALTH_CHECK, true);
        connect(url);
    }

    @Override
    public List<URL> discover(URL url) {
        String application = url.getParameter(Key.APPLICATION);
        List<URL> urls;
        // 1、判断是否缓存中是否有可用的服务
        List<String> serverUrls = discoverHealthServices.get(application);
        if (serverUrls == null || serverUrls.isEmpty()) {
            boolean noSubscribe = serverUrls == null;
            // 2、注册中心中发现可用服务
            urls = doDiscover(url);
            serverUrls = urls.stream().map(URL::toString).toList();
            // 3、订阅服务的变更
            if (noSubscribe) subscribeService(url);
            discoverHealthServices.put(application, serverUrls);
        } else {
            urls = serverUrls.stream().map(URL::valueOf).filter(serverUrl -> serverUrl.protocol().equalsIgnoreCase(url.protocol())).toList();
        }
        return urls;
    }

    public Map<String, String> metaInfo(URL url) {
        Virtue virtue = Virtue.getDefault();
        Map<String, String> systemInfo = virtue.newSystemInfo().toMap();
        HashMap<String, String> registryMeta = new HashMap<>(systemInfo);
        registryMeta.put(Key.PROTOCOL, url.protocol());
        registryMeta.put(Key.WEIGHT, String.valueOf(virtue.appConfig().weight()));
        return registryMeta;
    }

    protected abstract void subscribeService(URL url);

    protected abstract List<URL> doDiscover(URL url);

}
