package io.virtue.common.util;

import io.virtue.common.constant.SystemKey;
import io.virtue.common.exception.CommonException;

import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Utility class for net operations.
 */
public final class NetUtil {

    // Regular expression foR ip address
    private static final String IP_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    // Regular expression for a port number
    private static final String PORT_REGEX = "^\\d{1,5}$";
    private static volatile InetAddress LOCAL_ADDRESS = null;

    private NetUtil() {
    }

    /**
     * Determines whether a given string is a valid combination of IP address and port number.
     *
     * @param ipPort
     * @return
     */
    public static boolean isValidIpPort(String ipPort) {
        String[] parts = ipPort.split(":");
        if (parts.length != 2) {
            return false;
        }
        String ip = parts[0];
        String port = parts[1];
        return isValidIp(ip) && isValidPort(port);
    }

    /**
     * Determine if a given string is a legitimate IP address.
     *
     * @param ip
     * @return
     */
    public static boolean isValidIp(String ip) {
        return Pattern.matches(IP_REGEX, ip);
    }

    /**
     * Determine whether a given string is a valid port number.
     *
     * @param port
     * @return
     */
    public static boolean isValidPort(String port) {
        return Pattern.matches(PORT_REGEX, port);
    }

    /**
     * Converts a given InetSocketAddress object to a string representation.
     *
     * @param address
     * @return
     */
    public static String getAddress(InetSocketAddress address) {
        return (isLoopbackAddress(address.getHostString()) ? getLocalHost() : address.getHostString()) + ":" + address.getPort();
    }

    /**
     * Determine whether two InetSocketAddress objects are the same.
     *
     * @param addr1
     * @param addr2
     * @return
     */
    public static boolean isSameAddress(InetSocketAddress addr1, InetSocketAddress addr2) {
        if (addr1 == null || addr2 == null) {
            return false;
        }
        return addr1.getAddress().equals(addr2.getAddress()) && addr1.getPort() == addr2.getPort();
    }

    /**
     * If it is a loopback address.
     *
     * @param host
     * @return
     */
    public static boolean isLoopbackAddress(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false; // 可能的处理逻辑，根据实际情况返回
        }
    }

    /**
     * Convert the given string to an InetSocketAddress object.
     *
     * @param address
     * @return
     */
    public static InetSocketAddress toInetSocketAddress(String address) {
        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid address format: " + address);
        }
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);
        return new InetSocketAddress(ip, port);
    }

    /**
     * Converts the given hostname and port number into a string representation.
     *
     * @param host
     * @param port
     * @return
     */
    public static String getAddress(String host, int port) {
        return host + ":" + port;
    }

    /**
     * Get the local host.
     *
     * @return
     */
    public static String getLocalHost() {
        String localHost = System.getProperty(SystemKey.LOCAL_IP);
        if (!StringUtil.isBlank(localHost)) {
            return localHost;
        }
        InetAddress localAddress = getFirstLocalAddress();
        if (localAddress != null) {
            return localAddress.getHostAddress();
        }
        return null;
    }

    /**
     * Get the first local address of the machine.
     *
     * @return
     */
    public static InetAddress getFirstLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // Exclude loopback interfaces and disabled interfaces
                if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        // Exclude loopback addresses and IPv6 addresses
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                            LOCAL_ADDRESS = inetAddress;
                            return LOCAL_ADDRESS;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new CommonException(e);
        }
        return null;
    }
}
