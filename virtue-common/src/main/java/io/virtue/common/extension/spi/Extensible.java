package io.virtue.common.extension.spi;

import io.virtue.common.url.URL;

import java.lang.annotation.*;

/**
 * Use the annotation on the Interface,
 * Indicate that the current interface is an SPI Interface.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Extensible {

    /**
     * Default Implementation class name.
     *
     * @return default name
     * @see Extension#value()
     */
    String value() default "";

    /**
     * When use {@link ExtensionLoader#loadExtension(Class, URL, LoadedListener[])},
     * then can choose from url params and if extensionName is null will use the default extension
     *
     * @return
     */
    String key() default "";

    /**
     * Whether lazy loading is enabled,
     * Distinguishing it from the JDK's SPI (Service Provider Interface).
     *
     * @return whether lazyLoad
     */
    boolean lazyLoad() default true;

}
