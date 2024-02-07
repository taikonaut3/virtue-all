package io.github.astro.virtue.config.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Accessors(fluent = true, chain = true)
@Setter
@Getter
public class RouterConfig {

    private Map<String, List<String>> map = new ConcurrentHashMap<>();



}
