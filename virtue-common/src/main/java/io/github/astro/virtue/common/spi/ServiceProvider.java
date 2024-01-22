package io.github.astro.virtue.common.spi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceProvider {

    String value() default "";

    /**
     * Specifies which interface to provide implementation
     *
     * @default: for all @ServiceInterface modified interfaces
     */
    Class<?>[] interfaces() default {};

}
