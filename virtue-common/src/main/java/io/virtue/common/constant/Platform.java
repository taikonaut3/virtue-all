package io.virtue.common.constant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Platform get virtue version and jvm status.
 */
public final class Platform {

    private static final String VIRTUE_VERSION;

    public static final AtomicBoolean JVM_SHUTTING_DOWN = new AtomicBoolean(false);

    static {
        try {
            InputStream stream = Platform.class.getClassLoader().getResourceAsStream("version");
            if (stream == null) {
                throw new IllegalStateException("Can‘t find version file");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            VIRTUE_VERSION = reader.readLine();
        } catch (Exception e) {
            throw new IllegalStateException("Can’t read version", e);
        }
    }

    private Platform() {
    }

    public static String virtueVersion() {
        return VIRTUE_VERSION;
    }

    public static void jvmShuttingDown() {
        JVM_SHUTTING_DOWN.compareAndSet(false, true);
    }

    public static boolean isJvmShuttingDown() {
        return JVM_SHUTTING_DOWN.get();
    }
}
