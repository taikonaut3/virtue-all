package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.ResourceException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.virtue.common.constant.Components.Compression.GZIP;
import static io.virtue.common.constant.Components.Envelope.*;
import static io.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.virtue.common.constant.Components.Serialization.*;

/**
 * Store mapping: string -> byte.
 */
public class ModeContainer {

    private static final Map<String, List<ModeBean>> modeMap = new ConcurrentHashMap<>();

    static {
        put(Key.SERIALIZATION, JDK, (byte) 1);
        put(Key.SERIALIZATION, JSON, (byte) 2);
        put(Key.SERIALIZATION, KRYO, (byte) 3);
        put(Key.SERIALIZATION, FURY, (byte) 4);
        put(Key.SERIALIZATION, MSGPACK, (byte) 5);
        put(Key.ENVELOPE, REQUEST, (byte) 1);
        put(Key.ENVELOPE, RESPONSE, (byte) 2);
        put(Key.ENVELOPE, HEARTBEAT, (byte) 3);
        put(Key.ENVELOPE, ERROR, (byte) -1);
        put(Key.PROTOCOL, VIRTUE, (byte) 1);
        put(Key.COMPRESSION, GZIP, (byte) 1);
    }

    public static Mode getMode(String key, String name) {
        List<ModeBean> values = modeMap.get(key);
        for (ModeBean value : values) {
            if (name.equals(value.name)) {
                return value;
            }
        }
        throw new ResourceException("Can't find Key: " + key + " name is " + name);
    }

    public static Mode getMode(String key, byte type) {
        List<ModeBean> values = modeMap.get(key);
        for (ModeBean value : values) {
            if (type == value.type) {
                return value;
            }
        }
        throw new ResourceException("Can't find Key: " + key + " type is " + type);
    }

    /**
     * Put mode into container.
     *
     * @param key
     * @param name
     * @param type
     */
    public static void put(String key, String name, byte type) {
        List<ModeBean> values = modeMap.computeIfAbsent(key, k -> new LinkedList<>());
        ModeBean modeBean = new ModeBean(name, type);
        values.add(modeBean);
    }

    public static Map<String, List<ModeBean>> getContainer() {
        return modeMap;
    }

    /**
     * Mode Wrapper.
     *
     * @param name
     * @param type
     */
    public record ModeBean(String name, byte type) implements Mode {

    }

}
