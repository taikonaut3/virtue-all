package io.github.astro.virtue.common.spi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceInterface {

    String value() default "";

    boolean lazyLoad() default true;

}
