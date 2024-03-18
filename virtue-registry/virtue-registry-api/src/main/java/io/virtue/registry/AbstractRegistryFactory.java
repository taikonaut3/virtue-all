package io.virtue.registry;

import io.virtue.common.url.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Map<String, RegistryService> registries = new ConcurrentHashMap<>();

    @Override
    public RegistryService get(URL url) {
        String uri = url.authority();
        RegistryService registryService = registries.get(uri);
        if (registryService == null) {
            registryService = create(url);
            registries.put(uri, registryService);
        } else {
            if (!registryService.isActive()) {
                registryService.connect(url);
            }
        }
        return registryService;
    }

    protected abstract RegistryService create(URL url);

}
