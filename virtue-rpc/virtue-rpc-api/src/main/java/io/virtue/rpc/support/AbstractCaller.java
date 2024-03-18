package io.virtue.rpc.support;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.ReflectUtil;
import io.virtue.config.Caller;
import io.virtue.config.CallerContainer;
import io.virtue.config.annotation.Config;
import io.virtue.config.filter.Filter;
import io.virtue.config.filter.FilterChain;
import io.virtue.config.manager.ConfigManager;
import io.virtue.config.manager.Virtue;
import io.virtue.rpc.protocol.Protocol;
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
    @Setter
    @Parameter(Key.COMPRESSION)
    protected String compression;

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

    @Override
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

    protected Config config() {
        return null;
    }

    protected abstract URL createUrl(URL url);

    protected abstract void doInit();

    private T parseAnnotation(Method method, Class<T> type) {
        AssertUtil.condition(ReflectUtil.findAnnotation(method,type)!=null, "Only support @" + type.getSimpleName() + " modify Method");
        return method.getAnnotation(type);
    }

    private Config getConfig() {
        if (method.isAnnotationPresent(Config.class)) {
            return method.getAnnotation(Config.class);
        }
        if (config() != null) {
            return config();
        }
        return ReflectUtil.getDefaultInstance(Config.class);
    }

    private void parseConfig(Config config) {
        serialize(config.serialize());
        compression(config.compression());
        filterChain = ExtensionLoader.loadService(FilterChain.class, config.filterChain());
        String[] filterNames = config.filters();
        ConfigManager manager = virtue.configManager();
        Optional.ofNullable(filterNames).ifPresent(names -> Arrays.stream(names).map(manager.filterManager()::get).filter(Objects::nonNull).forEach(this::addFilter));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + path() + "]." + method.toString();
    }
}
