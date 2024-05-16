package io.virtue.common.constant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Platform get virtue version and jvm status.
 */
public final class Platform {

    public static final AtomicBoolean JVM_SHUTTING_DOWN = new AtomicBoolean(false);
    private static final String VIRTUE_VERSION;

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

    /**
     * Get current version of virtue.
     *
     * @return
     */
    public static String virtueVersion() {
        return VIRTUE_VERSION;
    }

    /**
     * The modified status indicates that the JVM is currently being shut down.
     */
    public static void jvmShuttingDown() {
        JVM_SHUTTING_DOWN.compareAndSet(false, true);
    }

    /**
     * Check whether the current JVM is being shut down.
     *
     * @return
     */
    public static boolean isJvmShuttingDown() {
        return JVM_SHUTTING_DOWN.get();
    }
}
