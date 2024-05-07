package io.virtue.common.extension.spi;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.ReflectionUtil;
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
import static io.virtue.common.util.ClassUtil.getClassLoader;
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

    private final Map<String, ExtensionWrapper> extensionWrappers;

    private final Map<String, S> extensions;

    private final Class<S> type;

    private final String defaultExtension;

    private final boolean lazyLoad;

    private ExtensionLoader(Class<S> type) {
        this.type = type;
        this.defaultExtension = type.getAnnotation(Extensible.class).value();
        this.lazyLoad = type.getAnnotation(Extensible.class).lazyLoad();
        this.extensions = new ConcurrentHashMap<>();
        this.extensionWrappers = new ConcurrentHashMap<>();
        loadExtensionWrappers(type);
    }

    /**
     * Load extension with listeners.
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
            throw new IllegalArgumentException("extension type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("extension type(" + type + ") is not interface");
        }
        if (!type.isAnnotationPresent(Extensible.class)) {
            throw new IllegalArgumentException("extension type(" + type + ") can not be load because WITHOUT @extensionInterface");
        }

        ExtensionLoader<?> extensionLoader = LOADED_MAP.computeIfAbsent(type.getTypeName(), k -> new ExtensionLoader<>(type));
        addListener(type, loadedListeners);
        return (ExtensionLoader<S>) extensionLoader;
    }

    /**
     * Load extension with extensionName and listeners.
     *
     * @param type
     * @param extensionName
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> S loadExtension(Class<S> type, String extensionName, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getExtension(extensionName);
    }

    /**
     * Load default extension with listeners.
     *
     * @param type
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> S loadExtension(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getDefault();
    }

    /**
     * Load all extensions with listeners.
     *
     * @param type
     * @param loadedListeners
     * @param <S>
     * @return
     */
    @SafeVarargs
    public static <S> List<S> loadExtensions(Class<S> type, LoadedListener<S>... loadedListeners) {
        return load(type, loadedListeners).getExtensions();
    }

    /**
     * Add listener for extension.
     *
     * @param interfaceType
     * @param loadedListeners
     * @param <S>
     */
    @SafeVarargs
    public static <S> void addListener(Class<S> interfaceType, LoadedListener<S>... loadedListeners) {
        if (CollectionUtil.isNotEmpty(loadedListeners)) {
            Set<LoadedListener<?>> loadedListenerSet = LISTENERMAP.computeIfAbsent(interfaceType, k -> new HashSet<>());
            Collections.addAll(loadedListenerSet, loadedListeners);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadExtensionWrappers(Class<S> type) {
        /**========================load extensions from {@link PREFIX}========================*/
        try {
            ClassLoader classLoader = getClassLoader(type);
            Enumeration<URL> resources = classLoader.getResources(PREFIX + type.getTypeName());
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                /**=======================load legal extensions to {@link extensions}========================*/
                try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String extensionClassName;
                    while ((extensionClassName = br.readLine()) != null) {
                        Class<?> extensionClass = classLoader.loadClass(extensionClassName);
                        if (!this.type.isAssignableFrom(extensionClass)) {
                            throw new IllegalArgumentException(format("Load extension type(%s) failed, %s is not %s’s subclass",
                                    this.type, type, this.type));
                        }
                        if (extensionClass.isAnnotationPresent(Extension.class)) {
                            Extension extension = extensionClass.getAnnotation(Extension.class);
                            String[] values = extension.value();
                            values = Arrays.stream(values).distinct().toArray(String[]::new);
                            if (values.length == 0) {
                                values = new String[]{extensionClassName};
                            }
                            ExtensionWrapper wrapper = new ExtensionWrapper((Class<? extends S>) extensionClass);
                            if (!lazyLoad) {
                                S instance = createInstance(wrapper);
                                for (String key : values) {
                                    extensionWrappers.put(key, wrapper);
                                    extensions.putIfAbsent(key, instance);
                                }
                            } else {
                                for (String key : values) {
                                    extensionWrappers.put(key, wrapper);
                                }
                            }
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
     * Get extension by extensionName.
     *
     * @param extensionName
     * @return
     */
    public S getExtension(String extensionName) {
        ExtensionWrapper wrapper = extensionWrappers.get(extensionName);
        if (wrapper == null) {
            throw new IllegalArgumentException(format("Load extension type(%s:%s) failed,Unknown the [%s] Please check is Exist",
                    this.type, extensionName, extensionName));
        }
        if (!this.type.isAssignableFrom(wrapper.type())) {
            throw new IllegalArgumentException(format("Load extension type(%s:%s) failed,%s is not %s’s subclass",
                    this.type, extensionName, wrapper.type(), this.type));
        }
        if (wrapper.extension().scope() == Scope.PROTOTYPE) {
            return createInstance(wrapper);
        }
        S extension = extensions.computeIfAbsent(extensionName, k -> createInstance(wrapper));
        for (String value : wrapper.values()) {
            extensions.putIfAbsent(value, extension);
        }
        return extension;
    }

    /**
     * Get default extension.
     *
     * @return
     */
    public S getDefault() {
        return getExtension(defaultExtension);
    }

    /**
     * Get all extensions.
     *
     * @return
     */
    public List<S> getExtensions() {
        ArrayList<S> list = new ArrayList<>();
        for (String key : extensionWrappers.keySet()) {
            list.add(getExtension(key));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private S createInstance(ExtensionWrapper wrapper) {
        S extension = ReflectionUtil.createInstance(wrapper.noArgsConstructor);
        Set<LoadedListener<?>> listeners = LISTENERMAP.get(type);
        if (listeners != null) {
            for (LoadedListener<?> listener : listeners) {
                ((LoadedListener<S>) listener).listen(extension);
            }
        }
        return extension;
    }

    @Getter
    @Accessors(fluent = true)
    private class ExtensionWrapper {

        private final Class<? extends S> type;

        private final Extension extension;

        private final List<String> values;

        private final Constructor<S> noArgsConstructor;

        @SuppressWarnings("unchecked")
        ExtensionWrapper(Class<? extends S> type) {
            this.type = type;
            this.extension = type.getAnnotation(Extension.class);
            this.values = Arrays.stream(extension.value()).distinct().toList();
            this.noArgsConstructor = (Constructor<S>) ReflectionUtil.finfConstructor(type, new Class[]{});
        }
    }
}
