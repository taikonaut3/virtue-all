package io.virtue.common.spi;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.ReflectUtil;
import io.virtue.common.util.StringUtil;
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
public class ExtensionLoader<S> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String PREFIX = SPI_FIX_PATH;

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private static final Map<String, ExtensionLoader<?>> LOADED_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Set<LoadedListener<?>>> LISTENERMAP = new ConcurrentHashMap<>();

    private final Map<String, S> services;

    private final Map<String, Class<? extends S>> serviceClasses;


    private final Class<S> type;

    private final String defaultService;

    private final boolean lazyLoad;
    private Object[] args;

    private ExtensionLoader(Class<S> type) {
        this.type = type;
        this.defaultService = type.getAnnotation(ServiceInterface.class).value();
        this.lazyLoad = type.getAnnotation(ServiceInterface.class).lazyLoad();
        this.services = new ConcurrentHashMap<>();
        this.serviceClasses = new ConcurrentHashMap<>();
        loadServiceClasses(type);
    }

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

    @SafeVarargs
    public static <S> S loadService(Class<S> type, String serviceName, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getService(serviceName);
    }

    @SafeVarargs
    public static <S> S loadService(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getDefault();
    }

    @SafeVarargs
    public static <S> List<S> loadServices(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getServices();
    }

    @SafeVarargs
    public static <S> void addListener(Class<S> interfaceType, LoadedListener<S>... loadedListeners) {
        if (loadedListeners != null && loadedListeners.length > 0) {
            Set<LoadedListener<?>> loadedListenerSet = LISTENERMAP.computeIfAbsent(interfaceType, k -> new HashSet<>());
            Collections.addAll(loadedListenerSet, loadedListeners);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadServiceClasses(Class<S> type) {
        /**========================load services from {@link PREFIX}========================*/
        try {
            Enumeration<URL> resources = classLoader.getResources(PREFIX + type.getTypeName());
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                /**=======================load legal services to {@link services}========================*/
                try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String serviceClassName;
                    while ((serviceClassName = br.readLine()) != null) {

                            Class<?> serviceClass = classLoader.loadClass(serviceClassName);
                            if (serviceClass.isAnnotationPresent(ServiceProvider.class)) {
                                ServiceProvider provider = serviceClass.getAnnotation(ServiceProvider.class);
                                String key = StringUtil.isBlankOrDefault(provider.value(), serviceClass.getName());
                                if(!lazyLoad){
                                    if (this.type.isAssignableFrom(serviceClass)) {
                                        services.putIfAbsent(key, createInstance((Class<? extends S>)serviceClass));
                                    } else {
                                        throw new IllegalArgumentException(format("Load service type(%s:%s) failed, %s is not %s’s subclass", this.type, key, type, this.type));
                                    }
                                }
                                serviceClasses.put(key, (Class<? extends S>) serviceClass);
                            }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Load " + type + " error", e);
            throw RpcException.unwrap(e);
        }

    }

    public S getService(String serviceName) {
        S service = services.get(serviceName);
        if(Objects.nonNull(service)){
            return service;
        }
        synchronized(serviceName.intern()) {
            service = services.get(serviceName);
            if (Objects.nonNull(service)) {
                return service;
            }
            Class<? extends S> type = serviceClasses.get(serviceName);
            if (type == null) {
                throw new IllegalArgumentException(format("Load service type(%s:%s) failed,Unknown the [%s] Please check is Exist", this.type, serviceName, serviceName));
            }
            if (this.type.isAssignableFrom(type)) {
                service = createInstance(type);
                services.putIfAbsent(serviceName, service);
            } else {
                throw new IllegalArgumentException(format("Load service type(%s:%s) failed,%s is not %s’s subclass", this.type, serviceName, type, this.type));
            }
        }
        return service;
    }

    public S getDefault() {
        return getService(defaultService);
    }

    public List<S> getServices() {
        ArrayList<S> list = new ArrayList<>();
        for (String key : serviceClasses.keySet()) {
            list.add(getService(key));
        }
        return list;
    }

    public ExtensionLoader<S> conditionOnConstructor(Object... args) {
        this.args = args;
        return this;
    }

    @SuppressWarnings("unchecked")
    private S createInstance(Class<? extends S> type) {
        ServiceProvider provider = type.getAnnotation(ServiceProvider.class);
        Class<?>[] constructorParameters = provider.constructor();
        try {
            S service = null;
            if (constructorParameters != null && constructorParameters.length > 0) {
                if (args != null && args.length > 0) {
                    Constructor<? extends S> constructor = ReflectUtil.finfConstructor(type, constructorParameters);
                    service = ReflectUtil.createInstance(constructor, args);
                }
            } else {
                service = ReflectUtil.createInstance(type);
            }
            Set<LoadedListener<?>> listeners = LISTENERMAP.get(type);
            if (listeners != null) {
                for (LoadedListener<?> listener : listeners) {
                    ((LoadedListener<S>) listener).listen(service);
                }
            }
            return service;
        } catch (Exception e) {
            throw RpcException.unwrap(e);
        }
    }

}
