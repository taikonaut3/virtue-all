package io.virtue.rpc.support;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.core.Invoker;
import io.virtue.core.InvokerContainer;
import io.virtue.core.Virtue;
import io.virtue.core.annotation.Config;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterChain;
import io.virtue.core.manager.ConfigManager;
import io.virtue.rpc.protocol.Protocol;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Abstract Invoker.
 *
 * @param <T>
 */
@Getter
@Accessors(fluent = true, chain = true)
public abstract class AbstractInvoker<T extends Annotation> extends AbstractAccessor implements Invoker<T> {

    protected final Virtue virtue;

    protected InvokerContainer container;

    protected Method method;

    protected volatile List<Filter> filters;

    protected Protocol protocolInstance;

    protected FilterChain filterChain;

    protected T parsedAnnotation;

    protected URL url;

    protected volatile boolean isStart = false;

    @Parameter(Key.LOCAL_VIRTUE)
    protected String virtueName;

    @Setter
    protected String protocol;

    @Setter
    @Parameter(Key.APPLICATION)
    protected String remoteApplication;

    @Setter
    @Parameter(Key.SERIALIZATION)
    protected String serialization;

    @Setter
    @Parameter(Key.COMPRESSION)
    protected String compression;

    protected AbstractInvoker(Method method, InvokerContainer container, String protocol, Class<T> annoType) {
        AssertUtil.notNull(method, container, protocol, annoType);
        this.parsedAnnotation = parseAnnotation(method, annoType);
        this.virtue = container.virtue();
        this.virtueName = virtue.name();
        this.method = method;
        this.container = container;
        this.remoteApplication = container.remoteApplication();
        this.protocol = protocol;
        this.protocolInstance = ExtensionLoader.loadExtension(Protocol.class, protocol);
        parseConfig(config());
        init();
        if (url != null) {
            url.set(Virtue.LOCAL_VIRTUE, virtue);
        }
    }

    @Override
    public void addFilter(Filter... filters) {
        if (this.filters == null) {
            synchronized (this) {
                if (this.filters == null) {
                    this.filters = new LinkedList<>();
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

    @Override
    public String toString() {
        return String.format("['%s'] -> %s", path(), GenerateUtil.generateKey(method));
    }

    protected abstract URL createUrl(URL url);

    protected abstract void doInit();

    private T parseAnnotation(Method method, Class<T> type) {
        AssertUtil.condition(ReflectionUtil.findAnnotation(method, type) != null,
                "Only support @" + type.getSimpleName() + " modify Method");
        return method.getAnnotation(type);
    }

    private Config config() {
        if (method.isAnnotationPresent(Config.class)) {
            return method.getAnnotation(Config.class);
        }
        return ReflectionUtil.getDefaultInstance(Config.class);
    }

    private void parseConfig(Config config) {
        serialization(config.serialization());
        compression(config.compression());
        filterChain = ExtensionLoader.loadExtension(FilterChain.class, config.filterChain());
        String[] filterNames = config.filters();
        ConfigManager manager = virtue.configManager();
        Optional.ofNullable(filterNames)
                .ifPresent(names ->
                        Arrays.stream(names)
                                .map(manager.filterManager()::get)
                                .filter(Objects::nonNull)
                                .forEach(this::addFilter));
    }

}
