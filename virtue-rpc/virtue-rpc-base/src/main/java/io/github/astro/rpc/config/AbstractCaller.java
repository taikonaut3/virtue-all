package io.github.astro.rpc.config;

import io.github.astro.rpc.protocol.Protocol;
import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.CollectionUtil;
import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.config.CallerContainer;
import io.github.astro.virtue.config.Invoker;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterChain;
import io.github.astro.virtue.config.manager.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Getter
@Accessors(fluent = true, chain = true)
public abstract class AbstractCaller<T extends Annotation> implements Caller<T> {

    protected final Virtue virtue;
    protected CallerContainer container;
    protected Method method;
    protected final List<Filter> filters = new LinkedList<>();
    protected final List<RegistryConfig> registryConfigs = new ArrayList<>();
    protected URL url;
    protected Protocol protocolInstance;
    protected FilterChain filterChain;
    protected volatile boolean isStart = false;

    protected T parsedAnnotation;

    @Setter
    protected String protocol;
    @Setter
    protected Invoker<?> invoker;
    @Setter
    @Parameter(Key.APPLICATION)
    protected String remoteApplication;
    @Setter
    @Parameter(Key.GROUP)
    protected String group;
    @Setter
    @Parameter(Key.VERSION)
    protected String version;
    @Setter
    @Parameter(Key.SERIALIZE)
    protected String serialize;

    protected AbstractCaller(Method method, CallerContainer container, String protocol, Class<T> annoType) {
        AssertUtil.notNull(method, container, protocol, annoType);
        this.parsedAnnotation = parseAnnotation(method, annoType);
        virtue = Virtue.getDefault();
        this.method = method;
        this.container = container;
        this.remoteApplication = container.remoteApplication();
        this.protocol = protocol;
        this.protocolInstance = ExtensionLoader.loadService(Protocol.class, protocol);
        init();
    }

    @Override
    public void start() {
        if (!isStart) {
            parseConfig(config());
            checkRegistryConfig();
            doStart();
            isStart = true;
        }
    }

    public void addFilter(Filter... filters) {
        CollectionUtil.addToList(this.filters, (oldFilter, newFilter) -> oldFilter == newFilter, filters);
    }

    public void addRegistryConfig(RegistryConfig... configs) {
        CollectionUtil.addToList(registryConfigs, (registryConfig, config) -> Objects.equals(config.type(), registryConfig.type()) && config.host().equals(registryConfig.host()) && config.port() == registryConfig.port(), configs);
    }

    @Override
    public Type returnType() {
        return method.getGenericReturnType();
    }

    protected abstract Config config();

    protected abstract URL createUrl();

    protected abstract void doInit();

    protected abstract void doStart();

    private T parseAnnotation(Method method, Class<T> type) {
        AssertUtil.condition(method.isAnnotationPresent(type), "Only support @" + type.getSimpleName() + " modify Method");
        return method.getAnnotation(type);
    }

    private void parseConfig(Config config) {
        serialize(config.serialize());
        filterChain = ExtensionLoader.loadService(FilterChain.class, config.filterChain());
        String[] registryNames = config.registries();
        String[] filterNames = config.filters();
        ConfigManager manager = virtue.configManager();
        for (RegistryConfig registryConfig : manager.registryConfigManager().getApplicationScopeConfigs()) {
            addRegistryConfig(registryConfig);
        }
        Optional.ofNullable(registryNames).ifPresent(names -> Arrays.stream(names).map(manager.registryConfigManager()::get).filter(Objects::nonNull).forEach(this::addRegistryConfig));
        Optional.ofNullable(filterNames).ifPresent(names -> Arrays.stream(names).map(manager.filterManager()::get).filter(Objects::nonNull).forEach(this::addFilter));
    }

    private void checkRegistryConfig() {
        if (registryConfigs.isEmpty()) {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.type(Constant.DEFAULT_REGISTRY);
            registryConfig.setAddress(Constant.LOCAL_CONSUL_ADDRESS);
            addRegistryConfig(registryConfig);
        }
    }
}
