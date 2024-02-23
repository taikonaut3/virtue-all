package io.github.taikonaut3.virtue.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("unchecked")
public final class ReflectUtil {

    public static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    private static final Map<Class<? extends Annotation>, Annotation> ANNOTATION_MAP = new LinkedHashMap<>();

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
            logger.error("Create Instance Fail", e);
            throw new RuntimeException(e);
        }

    }

    public static <T> T createInstance(Constructor<T> constructor, Object... args) {
        T instance = null;
        try {
            instance = constructor.newInstance(args);
        } catch (Exception e) {
            logger.error("Create Instance Fail", e);
        }
        return instance;
    }

    public static <T extends Annotation> T getDefaultInstance(Class<T> annotationType) {
        Annotation annotation = ANNOTATION_MAP.get(annotationType);
        if (annotation == null) {
            annotation = (T) Proxy.newProxyInstance(
                    annotationType.getClassLoader(),
                    new Class<?>[]{annotationType},
                    (proxy, method, args) -> {
                        if (method.getName().equals("toString")) {
                            return formatAnnotationToString(annotationType, proxy);
                        }
                        // 返回注解的默认属性值
                        return method.getDefaultValue();
                    });
            ANNOTATION_MAP.put(annotationType, annotation);
        }
        return (T) annotation;

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
    public static List<Field> getAllFields(Class<?> clazz){
        List<Field> allFields = new ArrayList<>();
        do{
            Field[] declaredFields = clazz.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            clazz = clazz.getSuperclass();
        }while(clazz != null && clazz != Object.class);
        return allFields;
    }

    public static <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement,Class<T> annotationType) {
        T annotation = annotatedElement.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        List<Annotation> annotations = Arrays.stream(annotatedElement.getAnnotations()).
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

    public static String formatAnnotationToString(Class<?> annotationType, Object proxy) {
        StringBuilder sb = new StringBuilder();
        sb.append('@').append(annotationType.getName()).append('(');
        Method[] methods = annotationType.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            try {
                method.setAccessible(true);
                Object value = method.invoke(proxy);
                sb.append(method.getName()).append('=').append(valueToString(value));
                if (i < methods.length - 1) {
                    sb.append(", ");
                }
            } catch (Exception e) {
                logger.error("{} toString error", annotationType);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    private static String valueToString(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                return Arrays.deepToString((Object[]) value);
            } else if (value instanceof boolean[]) {
                return Arrays.toString((boolean[]) value);
            } else if (value instanceof byte[]) {
                return Arrays.toString((byte[]) value);
            } else if (value instanceof char[]) {
                return Arrays.toString((char[]) value);
            } else if (value instanceof double[]) {
                return Arrays.toString((double[]) value);
            } else if (value instanceof float[]) {
                return Arrays.toString((float[]) value);
            } else if (value instanceof int[]) {
                return Arrays.toString((int[]) value);
            } else if (value instanceof long[]) {
                return Arrays.toString((long[]) value);
            } else if (value instanceof short[]) {
                return Arrays.toString((short[]) value);
            }
        }
        return String.valueOf(value);
    }

}
