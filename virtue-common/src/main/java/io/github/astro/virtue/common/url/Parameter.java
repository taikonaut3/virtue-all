package io.github.astro.virtue.common.url;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Parameter {

    String value();

}
