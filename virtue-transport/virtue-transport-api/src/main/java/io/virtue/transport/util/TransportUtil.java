package io.virtue.transport.util;

import io.virtue.common.url.URL;

import java.util.List;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Transport Util.
 */
public class TransportUtil {

    public static String getScheme(URL url) {
        String protocol = url.protocol();
        if (protocol.equals(H2)) {
            return "https";
        } else if (protocol.equals(H2C)) {
            return "http";
        }
        throw new IllegalArgumentException("Unsupported protocol: " + protocol);
    }

    /**
     * Check whether it is SSL.
     *
     * @param url
     * @return
     */
    public static boolean sslEnabled(URL url) {
        List<String> sslProtocols = List.of(H2, HTTPS);
        return sslProtocols.contains(url.protocol());
    }

}
