package io.github.astro.virtue.registry;

import io.github.astro.virtue.common.url.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {

    private final Map<String, URL> servicesCache = new ConcurrentHashMap<>();

    public void put(String path, URL url) {
        servicesCache.put(path, url);
    }

    public URL get(String path) {
        return servicesCache.get(path);
    }

    public void remove(String path) {
        servicesCache.remove(path);
    }

}
