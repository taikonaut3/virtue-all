package io.github.astro.virtue.common.spi;

@FunctionalInterface
public interface LoadedListener<T> {

    void listen(T service);

}
