package io.github.astro.virtue.common.extension;

import java.util.Map;

public interface Accessor<V> {

    void setData(Map<String, V> params);

    void replaceAccessor(Map<String, V> params);

    void setData(String key, V value);

    V getData(String key);

    V getData(String key, V defaultValue);

    V getDataOrPut(String key, V value);

    Map<String, V> getAccessor();

}
