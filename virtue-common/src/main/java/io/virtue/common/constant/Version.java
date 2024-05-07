package io.virtue.common.constant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Get current version.
 * @see maven-parent.revision
 */
public final class Version {

    private static final String VERSION;

    static {
        try {
            InputStream stream = Version.class.getClassLoader().getResourceAsStream("version");
            if (stream == null) {
                throw new IllegalStateException("Can‘t find version file");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            VERSION = reader.readLine();
        } catch (Exception e) {
            throw new IllegalStateException("Can’t read version", e);
        }
    }

    private Version() {
    }

    public static String version() {
        return VERSION;
    }
}
