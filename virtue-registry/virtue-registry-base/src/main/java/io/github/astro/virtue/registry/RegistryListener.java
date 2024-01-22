package io.github.astro.virtue.registry;

import io.github.astro.virtue.common.url.URL;

import java.util.List;

@FunctionalInterface
public interface RegistryListener {

    void listenChanged(String key, List<URL> oldUrls, List<URL> newUrls);

}
