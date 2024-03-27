package io.virtue.common.spi;

import java.lang.annotation.*;

/**
 * Use the annotation on the Interface,
 * Indicate that the current interface is an SPI Interface.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceInterface {

    /**
     * Default Implementation class name.
     *
     * @return default name
     * @see ServiceProvider#value()
     */
    String value() default "";


    /**
     * If this type of constructor exists, then create instance with this constructor,
     * or use default.
     *
     * @return constructor constructor type
     */
    Class<?>[] constructor() default {};


    /**
     * Whether lazy loading is enabled,
     * Distinguishing it from the JDK's SPI (Service Provider Interface).
     *
     * @return whether lazyLoad
     */
    boolean lazyLoad() default true;

}
