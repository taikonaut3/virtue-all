package io.virtue.registry;

import io.virtue.common.url.URL;
import io.virtue.core.Virtue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract RegistryFactory.
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Map<String, RegistryService> registries = new ConcurrentHashMap<>();

    @Override
    public RegistryService get(URL url) {
        String uri = url.authority();
        RegistryService registryService = registries.get(uri);
        if (registryService == null) {
            registryService = create(url);
            registries.put(uri, registryService);
            Virtue virtue = Virtue.ofLocal(url);
            virtue.register(registryService);
        } else {
            if (!registryService.isActive()) {
                registryService.connect(url);
            }
        }
        return registryService;
    }

    protected abstract RegistryService create(URL url);

}
