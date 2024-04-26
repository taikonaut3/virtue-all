package io.virtue.common.util;

import io.virtue.common.exception.CommonException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Utility class for reflection operations.
 */
@SuppressWarnings("unchecked")
public final class ReflectionUtil {

    // stores some commonly used annotation instances
    private static final Map<Class<? extends Annotation>, Annotation> DEFAULT_ANNOTATION_MAP = new LinkedHashMap<>();

    /**
     * Create an instance of the class.
     *
     * @param type The type of class to be created
     * @param args The argument to be passed to the class's constructor
     * @param <T>  The type of class
     * @return An instance of the class created
     */
    public static <T> T createInstance(Class<T> type, Object... args) {
        // Find the matching constructor
        Class<?>[] parameterTypes;
        try {
            parameterTypes = findMatchingConstructor(type, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // Create an instance of the class
        try {
            return parameterTypes == null ? type.getConstructor(new Class[]{}).newInstance()
                    : type.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            throw new CommonException("Create Instance is Failed for " + type.getName(), e);
        }
    }

    /**
     * Create an instance of the class.
     *
     * @param constructor Constructor to use
     * @param args        The argument to be passed to the class's constructor
     * @param <T>         The type of class
     * @return An instance of the class created
     */
    public static <T> T createInstance(Constructor<T> constructor, Object... args) {
        T instance;
        try {
            instance = CollectionUtil.isEmpty(args) ? constructor.newInstance() : constructor.newInstance(args);
        } catch (Exception e) {
            throw new CommonException("Create Instance is Failed by " + constructor.getName(), e);
        }
        return instance;
    }

    /**
     * Get the real class of the target class.
     *
     * @param type
     * @return
     */
    public static Class<?> getTargetClass(Class<?> type) {
        List<String> proxyNames = List.of("CGLIB", "ByteBuddy");
        for (String proxyName : proxyNames) {
            if (type.getName().contains(proxyName)) {
                Class<?> superclass = type.getSuperclass();
                if (superclass != null && superclass != Object.class) {
                    return superclass;
                }
            }
        }
        return type;
    }

    /**
     * Get the default instance of the annotation.
     *
     * @param annotationType The type of annotation to get
     * @param <T>            The type of annotation
     * @return The default instance of the annotation
     */
    public static <T extends Annotation> T getDefaultInstance(Class<T> annotationType) {
        // If a default instance of this type already exists, the system is returned
        Annotation annotation = DEFAULT_ANNOTATION_MAP.get(annotationType);
        if (annotation == null) {
            // Create a new default instance
            annotation = (T) Proxy.newProxyInstance(
                    annotationType.getClassLoader(),
                    new Class<?>[]{annotationType},
                    (proxy, method, args) -> {
                        if (method.getName().equals("toString")) {
                            // A string representation of the returned annotation
                            return formatAnnotationToString(annotationType, proxy);
                        }
                        // Returns the default attribute value for the annotation
                        return method.getDefaultValue();
                    });
            // Save the default instance
            DEFAULT_ANNOTATION_MAP.put(annotationType, annotation);
        }
        return (T) annotation;
    }

    /**
     * Find matching constructors.
     *
     * @param type The class to find
     * @param args Parameters to be passed to the constructor
     * @return matching constructor, if not found, returns null
     * @throws NoSuchMethodException If no matching constructor is found
     */
    public static Class<?>[] findMatchingConstructor(Class<?> type, Object... args) throws NoSuchMethodException {
        if (CollectionUtil.isEmpty(args)) {
            return null;
        }
        // Find all constructors
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            // Gets the parameter type of the constructor
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            // If the number of parameters is the same as the number of parameters to be passed,
            // the parameter type is matched
            if (parameterTypes.length == args.length) {
                int index = 0;
                while (index < args.length) {
                    // Get the parameter type
                    Class<?> parameterType = parameterTypes[index];
                    // Get the arguments you want to pass
                    Object obj = args[index];
                    // If the parameter type is assignable, the next parameter is matched
                    if (parameterType.isAssignableFrom(obj.getClass())) {
                        index++;
                    } else {
                        break;
                    }
                }
                // If the match is successful, the constructor is returned
                if (index == args.length) {
                    return constructor.getParameterTypes();
                }
            }
        }
        // If no matching constructor is found, an exception is thrown
        throw new NoSuchMethodException("Can't find the Constructor(" + Arrays.toString(args) + ")");
    }

    /**
     * Find the constructor for the class.
     *
     * @param type           The class to find
     * @param parameterTypes The parameter type of the constructor you want to find
     * @param <T>            The type of class
     * @return The class's constructor, if not found, returns null
     */
    public static <T> Constructor<T> finfConstructor(Class<T> type, Class<?>... parameterTypes) {
        Constructor<T> constructor = null;
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return constructor;
        }
        try {
            constructor = type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new CommonException("No matching constructor was found");
        }
        return constructor;
    }

    /**
     * Get all the fields of the class.
     *
     * @param type The class to get the field
     * @return All fields of the class
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> allFields = new ArrayList<>();
        do {
            Field[] declaredFields = type.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            type = type.getSuperclass();
        } while (type != null && type != Object.class);
        return allFields;
    }

    /**
     * Find other annotation specified in the annotatedElement(Class、Method、Field).
     *
     * @param annotatedElement The annotatedElement to find
     * @param annotationType   The type of annotation to find
     * @param <T>              The type of annotation
     * @return The other annotations specified in the annotatedElement are returned null if not found
     */
    public static <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement, Class<T> annotationType) {
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

    /**
     * Convert value to targetType.
     *
     * @param targetType
     * @param value
     * @param <T>
     * @return
     */
    public static <T> T convertValue(Class<T> targetType, Object value) {
        if (value == null) {
            return null;
        }
        // 如果目标类型和值的类型相同，直接返回值
        if (targetType.isAssignableFrom(value.getClass())) {
            return targetType.cast(value);
        }
        // 根据目标类型进行转换
        if (targetType == String.class) {
            return (T) value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(value.toString());
        } else if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(value.toString());
        } else if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(value.toString());
        } else if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(value.toString());
        } else if (targetType == Short.class || targetType == short.class) {
            return (T) Short.valueOf(value.toString());
        } else if (targetType == Byte.class || targetType == byte.class) {
            return (T) Byte.valueOf(value.toString());
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return (T) Boolean.valueOf(value.toString());
        } else if (targetType == Character.class || targetType == char.class) {
            if (value.toString().length() == 1) {
                return (T) Character.valueOf(value.toString().charAt(0));
            } else {
                throw new IllegalArgumentException("Cannot convert value to char: " + value);
            }
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }

    /**
     * Find other annotation specified in the annotation.
     *
     * @param annotation     The annotation to find
     * @param annotationType The type of annotation to find
     * @param <T>            The type of annotation
     * @return The other annotations specified in the annotation are returned null if not found
     */
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

    /**
     * A string representation of the returned annotation.
     *
     * @param annotationType The type of annotation
     * @param proxy          Proxy objects
     * @return The string representation of the annotation
     */
    private static String formatAnnotationToString(Class<?> annotationType, Object proxy) {
        StringBuilder sb = new StringBuilder();
        sb.append('@').append(annotationType.getName()).append('(');
        // All method to get annotations
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
                throw new CommonException(annotationType + " toString error");
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Convert an object to a string.
     *
     * @param value
     * @return
     */
    private static String valueToString(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value.getClass().isArray()) {
            switch (value) {
                case Object[] objects -> {
                    return Arrays.deepToString(objects);
                }
                case boolean[] booleans -> {
                    return Arrays.toString(booleans);
                }
                case byte[] bytes -> {
                    return Arrays.toString(bytes);
                }
                case char[] chars -> {
                    return Arrays.toString(chars);
                }
                case double[] doubles -> {
                    return Arrays.toString(doubles);
                }
                case float[] floats -> {
                    return Arrays.toString(floats);
                }
                case int[] ints -> {
                    return Arrays.toString(ints);
                }
                case long[] longs -> {
                    return Arrays.toString(longs);
                }
                case short[] shorts -> {
                    return Arrays.toString(shorts);
                }
                default -> {
                }
            }
        }
        return String.valueOf(value);
    }

    /**
     * Get class by the type.
     *
     * @param type
     * @return
     */
    public static Class<?> getClassByType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return (Class<?>) parameterizedType.getRawType();
        } else {
            return (Class<?>) type;
        }
    }
}
