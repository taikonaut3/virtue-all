package io.virtue.common.aot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Reflect Meta.
 */
@Data
@Accessors(fluent = true)
public class ReflectMeta {

    private final List<MethodMeta> methods = new LinkedList<>();

    private final List<String> fields = new LinkedList<>();

    private Class<?> type;

    private String name;

    private boolean allDeclaredConstructors = false;

    private boolean allPublicConstructors = false;

    private boolean allDeclaredMethods = false;

    private boolean allPublicMethods = false;

    private boolean allDeclaredFields = false;

    private boolean allPublicFields = false;

    public ReflectMeta(Class<?> type) {
        this.type = type;
        this.name = type.getName();
    }

    public ReflectMeta addMethod(String name, Class<?>... parameterTypes) {
        methods.add(new MethodMeta(name, parameterTypes));
        return this;
    }

    public ReflectMeta addFields(String... names) {
        Collections.addAll(fields, names);
        return this;
    }

    public void register() {
        RuntimeReflection.register(type);
        if (allDeclaredConstructors) RuntimeReflection.register(type.getDeclaredConstructors());
        if (allPublicConstructors) RuntimeReflection.register(type.getConstructors());
        if (allDeclaredMethods) RuntimeReflection.register(type.getDeclaredMethods());
        if (allPublicMethods) RuntimeReflection.register(type.getMethods());
        if (allDeclaredFields) RuntimeReflection.register(type.getDeclaredFields());
        if (allPublicFields) RuntimeReflection.register(type.getFields());
        methods.forEach(methodMeta -> {
            try {
                RuntimeReflection.register(type.getDeclaredMethod(methodMeta.name, methodMeta.parameterTypes));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        fields.forEach(name -> {
            try {
                RuntimeReflection.register(type.getDeclaredField(name));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor
    static class MethodMeta {

        private String name;

        private Class<?>[] parameterTypes;

    }

}
