package io.virtue.common.extension.spi;

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
     * Whether lazy loading is enabled,
     * Distinguishing it from the JDK's SPI (Service Provider Interface).
     *
     * @return whether lazyLoad
     */
    boolean lazyLoad() default true;

}
