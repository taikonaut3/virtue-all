package io.virtue.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class CollectionUtil {

    public static <T> void addToList(Collection<T> collection, BiPredicate<T, T> predicate, T... items) {
        addToList(collection, predicate, null, items);
    }

    public static <T> void addToList(Collection<T> collection, BiPredicate<T, T> predicate, Consumer<T> successCallBack, T... items) {
        loop:
        for (T item : items) {
            for (T collect : collection) {
                if (predicate.test(collect, item)) {
                    continue loop;
                }
            }
            collection.add(item);
            if (successCallBack != null) {
                successCallBack.accept(item);
            }
        }
    }

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

    private CollectionUtil(){}

}
