package io.virtue.common.url;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.ResourceException;
import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.common.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic for:
 * 1、Rpc Request.
 * 2、Connect to Third Party Middleware.
 */
@Getter
@Accessors(fluent = true)
public class URL extends AbstractAccessor {

    public static final AttributeKey<URL> ATTRIBUTE_KEY = AttributeKey.of(Key.URL);

    private static final Logger logger = LoggerFactory.getLogger(URL.class);

    private final List<String> paths;
    @Setter
    protected String host;
    @Setter
    protected int port;
    @Setter
    protected String protocol;
    protected String address;
    private Map<String, String> params;

    public URL() {
        this.paths = new LinkedList<>();
        this.params = new HashMap<>();
    }

    public URL(String protocol, InetSocketAddress address) {
        this();
        AssertUtil.notNull(protocol, address);
        this.protocol = protocol;
        this.host = address.getHostString();
        this.port = address.getPort();
        this.address = NetUtil.getAddress(host, port);
    }

    public URL(String protocol, InetSocketAddress address, Map<String, String> params) {
        this(protocol, address);
        addParams(params);
    }

    public URL(String protocol, String ip, int port) {
        this(protocol, new InetSocketAddress(ip, port));
    }

    public URL(String protocol, String ip, int port, Map<String, String> params) {
        this(protocol, new InetSocketAddress(ip, port), params);
    }

    public URL(String protocol, String address) {
        this();
        AssertUtil.notNull(protocol, address);
        this.protocol = protocol;
        address(address);
    }

    public URL(String protocol, String address, Map<String, String> params) {
        this(protocol, address);
        addParams(params);

    }

    /**
     * String convert to URL.
     *
     * @param url
     * @return
     */
    public static URL valueOf(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ResourceException("url is null");
        }
        String[] strings = url.split("\\?");
        String fixed = strings[0];
        int protocolStartIndex = fixed.lastIndexOf("://");
        String protocol = fixed.substring(0, protocolStartIndex);
        fixed = fixed.substring(protocolStartIndex + 3);
        String[] addressPath = fixed.split("/");
        String address = addressPath[0];
        URL urlObj = new URL(protocol, address);
        if (addressPath.length > 1) {
            for (int i = 1; i < addressPath.length; i++) {
                urlObj.addPath(addressPath[i]);
            }
        }
        if (strings.length > 1) {
            String params = strings[1];
            urlObj.addParams(paramsToMap(params));
        }
        return urlObj;
    }

    /**
     * Map convert to URL params.
     *
     * @param params
     * @return
     */
    public static String toUrlParams(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String decodeParam(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * URL params convert to map.
     *
     * @param params
     * @return
     */
    public static Map<String, String> paramsToMap(String params) {
        return Arrays.stream(params.split("&"))
                .map(param -> param.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        split -> decodeParam(split[0]),
                        split -> decodeParam(split[1])
                ));
    }

    /**
     * List paths convert path String.
     *
     * @param paths
     * @return
     */
    public static String toPath(List<String> paths) {
        if (paths == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            builder.append("/");
            builder.append(path);
        }
        return builder.toString();
    }

    /**
     * String path convert to List.
     *
     * @param path
     * @return
     */
    public static List<String> pathToList(String path) {
        List<String> list = new ArrayList<>();
        if (StringUtil.isBlank(path)) {
            return list;
        }
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty()) {
                list.add(part);
            }
        }
        return list;
    }

    /**
     * Set address.
     *
     * @param address
     * @return
     */
    public URL address(String address) {
        this.address = address;
        if (NetUtil.isValidIpPort(address)) {
            InetSocketAddress socketAddress = NetUtil.toInetSocketAddress(address);
            this.host = socketAddress.getHostString();
            this.port = socketAddress.getPort();
        }
        return this;
    }

    /**
     * Add path.
     *
     * @param path
     */
    public void addPath(String path) {
        if (StringUtil.isBlank(path)) {
            logger.warn("Add empty path, will skip");
        } else {
            path = StringUtil.normalizePath(path);
            paths.add(path);
        }
    }

    /**
     * Add path with index.
     *
     * @param index
     * @param path
     */
    public void addPath(int index, String path) {
        if (StringUtil.isBlank(path)) {
            logger.warn("Add empty path, will skip");
        } else {
            path = StringUtil.normalizePath(path);
            paths.add(index, path);
        }
    }

    /**
     * Add paths.
     *
     * @param paths
     */
    public void addPaths(List<String> paths) {
        paths.forEach(this::addPath);
    }

    /**
     * Remove path by index.
     *
     * @param index
     */
    public void removePath(int index) {
        if (paths.size() > index) {
            paths.remove(index);
        }
    }

    /**
     * Get path by index.
     *
     * @param index
     * @return
     */
    public String getPath(int index) {
        if (paths.size() > index) {
            return paths.get(index);
        }
        return null;
    }

    /**
     * Add param.
     *
     * @param key
     * @param value
     */
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    /**
     * Add params.
     *
     * @param params
     */
    public void addParams(Map<String, String> params) {
        if (params != null) this.params.putAll(params);
    }

    /**
     * Replace all params.
     *
     * @param params
     */
    public void replaceParams(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            this.params = params;
        }
    }

    /**
     * Get param by key,if not return null.
     *
     * @param key
     * @return
     */
    public String getParam(String key) {
        return params.get(key);
    }

    /**
     * Get param by key,if not return defaultValue.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getParam(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    /**
     * Get boolean param by key,if not return null.
     *
     * @param key
     * @return
     */
    public boolean getBooleanParam(String key) {
        return Boolean.parseBoolean(getParam(key));
    }

    /**
     * Get boolean param by key,if not return defaultValue.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanParam(String key, boolean defaultValue) {
        String value = getParam(key);
        return StringUtil.isBlank(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    /**
     * Get int param by key.
     *
     * @param key
     * @return
     */
    public int getIntParam(String key) {
        return Integer.parseInt(getParam(key));
    }

    /**
     * Get int param by key,if not return defaultValue.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntParam(String key, int defaultValue) {
        String value = getParam(key);
        return StringUtil.isBlank(value) ? defaultValue : Integer.parseInt(value);
    }

    /**
     * Get long param by key.
     *
     * @param key
     * @return
     */
    public long getLongParam(String key) {
        return Long.parseLong(getParam(key));
    }

    /**
     * Get long param by key,if not return defaultValue.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLongParam(String key, long defaultValue) {
        String value = getParam(key);
        return StringUtil.isBlank(value) ? defaultValue : Long.parseLong(value);
    }

    /**
     * Remove param by key.
     *
     * @param key
     */
    public void removeParam(String key) {
        params.remove(key);
    }

    /**
     * URL authority.
     *
     * @return
     */
    public String authority() {
        return protocol + "://" + address;
    }

    /**
     * URL uri.
     *
     * @return
     */
    public String uri() {
        return authority() + path();
    }

    /**
     * Replace all paths.
     *
     * @param paths
     */
    public void replacePaths(List<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            this.paths.clear();
            addPaths(paths);
        }
    }

    /**
     * URL path.
     *
     * @return
     */
    public String path() {
        return toPath(paths);

    }

    /**
     * URL path+params.
     *
     * @return
     */
    public String pathAndParams() {
        String paramsStr = toUrlParams(params);
        return path() + "?" + StringUtil.isBlankOrDefault(paramsStr, "");
    }

    /**
     * Deep copy,without {@link io.virtue.common.extension.Accessor}.
     *
     * @return
     */
    public URL deepCopy() {
        URL url = new URL(protocol, address(), params);
        url.replacePaths(paths);
        url.accessor.putAll(this.accessor);
        return url;
    }

    @Override
    public String toString() {
        return authority() + pathAndParams();
    }

}
