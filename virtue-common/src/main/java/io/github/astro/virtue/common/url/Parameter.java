package io.github.astro.virtue.common.url;

import java.lang.annotation.*;

/**
 * This annotation marks a field that can be put into a map with the corresponding value
 * by a class implementing the {@link Parameterization} interface.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Parameter {

    /**
     * Put into the map's Key
     *
     * @return key name
     */
    String value();

}
