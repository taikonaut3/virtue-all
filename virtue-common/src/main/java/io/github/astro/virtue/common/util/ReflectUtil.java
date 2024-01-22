package io.github.astro.virtue.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ReflectUtil {

    public static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    public static <T> T createInstance(Class<T> type, Object... args) {
        Class<?>[] parameterTypes;
        try {
            parameterTypes = findMatchingConstructor(type, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            return parameterTypes == null ? type.getConstructor(new Class[]{}).newInstance() :
                    type.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            logger.error("Create Instance Error", e);
            throw new RuntimeException(e);
        }

    }

    public static <T> T createInstance(Constructor<T> constructor, Object... args) {
        T instance = null;
        try {
            instance = constructor.newInstance(args);
        } catch (Exception e) {
            logger.error("Create Instance Error", e);
        }
        return instance;
    }

    public static Class<?>[] findMatchingConstructor(Class<?> clazz, Object... args) throws NoSuchMethodException {
        if (args == null || args.length == 0) {
            return null;
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == args.length) {
                int index = 0;
                while (index < args.length) {
                    Class<?> parameterType = parameterTypes[index];
                    Object obj = args[index];
                    if (parameterType.isAssignableFrom(obj.getClass())) {
                        index++;
                    } else {
                        break;
                    }
                }
                if (index == args.length) {
                    return constructor.getParameterTypes();
                }
            }
        }
        throw new NoSuchMethodException("Can't find the Constructor(" + Arrays.toString(args) + ")");
    }

    public static <T> Constructor<T> finfConstructor(Class<T> type, Class<?>... parameterTypes) {
        Constructor<T> constructor = null;
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return constructor;
        }
        try {
            constructor = type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            logger.error("No matching constructor was found");
        }
        return constructor;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(Arrays.asList(declaredFields));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            fields.addAll(getAllFields(superClass));
        }
        return fields;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationType) {
        T annotation = clazz.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        List<Annotation> annotations = Arrays.stream(clazz.getAnnotations()).
                filter(item -> !item.annotationType().getPackageName().startsWith("java.lang.annotation")).toList();
        for (Annotation anno : annotations) {
            annotation = findAnnotation(anno, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationType) {
        T annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        List<Annotation> annotations = Arrays.stream(method.getAnnotations()).
                filter(item -> !item.annotationType().getPackageName().startsWith("java.lang.annotation")).toList();
        for (Annotation anno : annotations) {
            annotation = findAnnotation(anno, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    private static <T extends Annotation> T findAnnotation(Annotation annotation, Class<T> annotationType) {
        Class<? extends Annotation> type = annotation.annotationType();
        if (type == annotationType) {
            return (T) annotation;
        }
        List<Annotation> innerAnnotations = Arrays.stream(type.getAnnotations()).
                filter(item -> !item.annotationType().getPackageName().startsWith("java.lang.annotation")).toList();
        for (Annotation innerAnnotation : innerAnnotations) {
            T result = findAnnotation(innerAnnotation, annotationType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
