package io.github.astro.virtue.common.extension;

public interface Accessor {

    <T> Attribute<T> attribute(AttributeKey<T> key);

    Accessor remove(String key);

}
