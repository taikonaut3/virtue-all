package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chang Liu
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> value){
        return Objects.isNull(value) || value.isEmpty();
    }

    public static boolean isEmpty(Map<?,?> value){
        return Objects.isNull(value) || value.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> value){
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(Map<?,?> value){
        return !isEmpty(value);
    }
}
