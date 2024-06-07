package io.virtue.registry;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.registry.support.RegisterServiceEvent;
import io.virtue.registry.support.RegisterTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Abstract RegistryService.
 */
public abstract class AbstractRegistryService implements RegistryService {

    protected final Map<String, List<String>> discoverHealthServices = new ConcurrentHashMap<>();

    protected final Map<String, URL> registeredUrls = new ConcurrentHashMap<>();

    protected URL registryUrl;

    protected boolean enableHealthCheck;

    protected AbstractRegistryService(URL url) {
        this.registryUrl = url;
        this.enableHealthCheck = url.getBooleanParam(Key.ENABLE_HEALTH_CHECK, true);
        connect(url);
    }

    @Override
    public void register(URL url) {
        Virtue virtue = Virtue.ofLocal(url);
        if (!registeredUrls.containsKey(url.authority())) {
            var task = createRegisterTask(url);
            RegisterTask registerTask = new RegisterTask(url, task);
            RegisterServiceEvent event = new RegisterServiceEvent(url, registerTask);
            virtue.eventDispatcher().dispatch(event);
            registeredUrls.put(url.authority(), url);
        }
    }

    @Override
    public List<URL> discover(URL url) {
        String serviceName = serviceName(url);
        List<URL> urls;
        // 1、Determine whether a service is available in the cache
        List<String> serverUrls = discoverHealthServices.get(serviceName);
        if (CollectionUtil.isEmpty(serverUrls)) {
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

    @Override
    public void close() {
        registeredUrls.values().forEach(this::deregister);
        discoverHealthServices.clear();
        registeredUrls.clear();
    }

    protected String instanceId(URL url) {
        return "<" + url.protocol() + ">" + url.address();
    }

    protected String serviceName(URL url) {
        String applicationName = url.getParam(Key.APPLICATION);
        if (StringUtil.isBlank(applicationName)) {
            throw new IllegalArgumentException("application name is null");
        }
        return applicationName;
    }

    protected abstract void subscribeService(URL url);

    protected abstract BiConsumer<RegisterTask, Map<String, String>> createRegisterTask(URL url);

    protected abstract List<URL> doDiscover(URL url);

}
