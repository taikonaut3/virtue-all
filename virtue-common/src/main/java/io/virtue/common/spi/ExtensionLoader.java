package io.virtue.common.spi;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.common.util.StringUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.virtue.common.constant.Constant.SPI_FIX_PATH;
import static java.lang.String.format;

/**
 * Extension spi loader.
 *
 * @param <S>
 */
public final class ExtensionLoader<S> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String PREFIX = SPI_FIX_PATH;

    private static final Map<String, ExtensionLoader<?>> LOADED_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Set<LoadedListener<?>>> LISTENERMAP = new ConcurrentHashMap<>();

    private final Map<String, ProviderWrapper> providerWrappers;

    private final Map<String, S> services;

    private final Class<S> type;

    private final String defaultService;

    private final boolean lazyLoad;

    private Object[] args;

    private ExtensionLoader(Class<S> type) {
        this.type = type;
        this.defaultService = type.getAnnotation(ServiceInterface.class).value();
        this.lazyLoad = type.getAnnotation(ServiceInterface.class).lazyLoad();
        this.services = new ConcurrentHashMap<>();
        this.providerWrappers = new ConcurrentHashMap<>();
        loadServiceProvider(type);
    }

    /**
     * Load service with listeners.
     *
     * @param type
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <S> ExtensionLoader<S> load(Class<S> type, LoadedListener<S>... loadedListeners) {
        if (type == null) {
            throw new IllegalArgumentException("Service type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Service type(" + type + ") is not interface");
        }
        if (!type.isAnnotationPresent(ServiceInterface.class)) {
            throw new IllegalArgumentException("Service type(" + type + ") can not be load because WITHOUT @ServiceInterface");
        }
        ExtensionLoader<?> extensionLoader = LOADED_MAP.get(type.getTypeName());
        if (extensionLoader == null) {
            extensionLoader = new ExtensionLoader<>(type);
            LOADED_MAP.putIfAbsent(type.getTypeName(), extensionLoader);
        }
        addListener(type, loadedListeners);
        return (ExtensionLoader<S>) extensionLoader;
    }

    /**
     * Load service with serviceName and listeners.
     *
     * @param type
     * @param serviceName
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> S loadService(Class<S> type, String serviceName, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getService(serviceName);
    }

    /**
     * Load default service with listeners.
     *
     * @param type
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> S loadService(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getDefault();
    }

    /**
     * Load all services with listeners.
     *
     * @param type
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> List<S> loadServices(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getServices();
    }

    /**
     * Add listener for service.
     *
     * @param interfaceType
     * @param loadedListeners
     * @param <S>
     */
    @SafeVarargs
    public static <S> void addListener(Class<S> interfaceType, LoadedListener<S>... loadedListeners) {
        if (loadedListeners != null && loadedListeners.length > 0) {
            Set<LoadedListener<?>> loadedListenerSet = LISTENERMAP.computeIfAbsent(interfaceType, k -> new HashSet<>());
            Collections.addAll(loadedListenerSet, loadedListeners);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadServiceProvider(Class<S> type) {
        /**========================load services from {@link PREFIX}========================*/
        try {
            Enumeration<URL> resources = classLoader().getResources(PREFIX + type.getTypeName());
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                /**=======================load legal services to {@link services}========================*/
                try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String serviceClassName;
                    while ((serviceClassName = br.readLine()) != null) {
                        Class<?> serviceClass = classLoader().loadClass(serviceClassName);
                        if (!this.type.isAssignableFrom(serviceClass)) {
                            throw new IllegalArgumentException(format("Load service type(%s) failed, %s is not %s’s subclass",
                                    this.type, type, this.type));
                        }
                        if (serviceClass.isAnnotationPresent(ServiceProvider.class)) {
                            ServiceProvider provider = serviceClass.getAnnotation(ServiceProvider.class);
                            String key = StringUtil.isBlankOrDefault(provider.value(), serviceClass.getName());
                            ProviderWrapper wrapper = new ProviderWrapper((Class<? extends S>) serviceClass);
                            if (!lazyLoad) {
                                services.putIfAbsent(key, createInstance(wrapper));
                            }
                            providerWrappers.put(key, wrapper);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Load " + type + " error", e);
            throw RpcException.unwrap(e);
        }

    }

    /**
     * Get service by serviceName.
     *
     * @param serviceName
     * @return
     */
    public S getService(String serviceName) {
        ProviderWrapper wrapper = providerWrappers.get(serviceName);
        if (wrapper == null) {
            throw new IllegalArgumentException(format("Load service type(%s:%s) failed,Unknown the [%s] Please check is Exist",
                    this.type, serviceName, serviceName));
        }
        if (!this.type.isAssignableFrom(wrapper.type())) {
            throw new IllegalArgumentException(format("Load service type(%s:%s) failed,%s is not %s’s subclass",
                    this.type, serviceName, wrapper.type(), this.type));
        }
        if (wrapper.provider().scope() == Scope.PROTOTYPE) {
            return createInstance(wrapper);
        }
        S service = services.get(serviceName);
        if (Objects.nonNull(service)) {
            return service;
        }
        synchronized (serviceName.intern()) {
            service = services.get(serviceName);
            if (Objects.nonNull(service)) {
                return service;
            }
            service = createInstance(wrapper);
            services.putIfAbsent(serviceName, service);

        }
        return service;
    }

    /**
     * Get default service.
     *
     * @return
     */
    public S getDefault() {
        return getService(defaultService);
    }

    /**
     * Get all services.
     *
     * @return
     */
    public List<S> getServices() {
        ArrayList<S> list = new ArrayList<>();
        for (String key : providerWrappers.keySet()) {
            list.add(getService(key));
        }
        return list;
    }

    /**
     * Condition on constructor.
     *
     * @param args
     * @return
     */
    public ExtensionLoader<S> conditionOnConstructor(Object... args) {
        this.args = args;
        return this;
    }

    @SuppressWarnings("unchecked")
    private S createInstance(ProviderWrapper wrapper) {
        S service = null;
        if (wrapper.constructor() != null) {
            if (args != null && args.length > 0) {
                service = ReflectionUtil.createInstance(wrapper.constructor(), args);
            }
        } else {
            service = ReflectionUtil.createInstance(wrapper.type());
        }
        Set<LoadedListener<?>> listeners = LISTENERMAP.get(type);
        if (listeners != null) {
            for (LoadedListener<?> listener : listeners) {
                ((LoadedListener<S>) listener).listen(service);
            }
        }
        return service;
    }

    private ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Getter
    @Accessors(fluent = true)
    private class ProviderWrapper {

        private final Class<? extends S> type;

        private final ServiceProvider provider;

        // If exist {@link ServiceInterface#constructor()}.
        private Constructor<? extends S> constructor;

        ProviderWrapper(Class<? extends S> type) {
            this.type = type;
            this.provider = type.getAnnotation(ServiceProvider.class);
            Class<?>[] constructorParameters = ExtensionLoader.this.type.getAnnotation(ServiceInterface.class).constructor();
            if (constructorParameters != null && constructorParameters.length > 0) {
                try {
                    constructor = ReflectionUtil.finfConstructor(type, constructorParameters);
                } catch (Exception ignored) {

                }
            }
        }
    }
}
