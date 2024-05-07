package io.virtue.common.util;

/**
 * Utility class for common class operations.
 */
public final class ClassUtil {

    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        if (!clazz.getName().startsWith("io.virtue")) {
            cl = clazz.getClassLoader();
        }
        if (cl == null) {
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (Exception ignored) {
                // Cannot access thread context ClassLoader - falling back to system class loader...
            }
            if (cl == null) {
                // No thread context class loader -> use class loader of this class.
                cl = clazz.getClassLoader();
                if (cl == null) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Exception ignored) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
        }
        return cl;
    }
}
