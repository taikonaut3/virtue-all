package io.github.astro.virtue.rpc.config;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.CollectionUtil;
import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.config.CallerContainer;
import io.github.astro.virtue.config.Invoker;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterChain;
import io.github.astro.virtue.config.manager.ConfigManager;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.rpc.protocol.Protocol;
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

    protected volatile List<Filter> filters;

    protected Protocol<?, ?> protocolInstance;

    protected FilterChain filterChain;

    protected T parsedAnnotation;

    protected URL url;

    protected volatile boolean isStart = false;

    @Parameter(Key.VIRTUE)
    protected String virtueName;

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
        virtue = container.virtue();
        virtueName = virtue.name();
        this.method = method;
        this.container = container;
        this.remoteApplication = container.remoteApplication();
        this.protocol = protocol;
        this.protocolInstance = ExtensionLoader.loadService(Protocol.class, protocol);
        parseConfig(getConfig());
        init();
        if (url != null) {
            url.attribute(Virtue.ATTRIBUTE_KEY).set(virtue);
        }
    }


    public void addFilter(Filter... filters) {
        if (this.filters == null) {
            synchronized (this) {
                if (this.filters == null) {
                    this.filters = new ArrayList<>();
                }
            }
        }
        CollectionUtil.addToList(this.filters, (oldFilter, newFilter) -> oldFilter == newFilter, filters);
    }

    @Override
    public Type returnType() {
        return method.getGenericReturnType();
    }

    @Override
    public Class<?> returnClass() {
        return method.getReturnType();
    }

    protected abstract Config config();

    protected abstract URL createUrl(URL url);

    protected abstract void doInit();

    private T parseAnnotation(Method method, Class<T> type) {
        AssertUtil.condition(method.isAnnotationPresent(type), "Only support @" + type.getSimpleName() + " modify Method");
        return method.getAnnotation(type);
    }

    private Config getConfig() {
        if (method.isAnnotationPresent(Config.class)) {
            return method.getAnnotation(Config.class);
        }
        return config();
    }

    private void parseConfig(Config config) {
        serialize(config.serialize());
        filterChain = ExtensionLoader.loadService(FilterChain.class, config.filterChain());
        String[] filterNames = config.filters();
        ConfigManager manager = virtue.configManager();
        Optional.ofNullable(filterNames).ifPresent(names -> Arrays.stream(names).map(manager.filterManager()::get).filter(Objects::nonNull).forEach(this::addFilter));
    }
}
