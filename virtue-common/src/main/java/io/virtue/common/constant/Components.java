package io.virtue.common.constant;

/**
 * SPI implementation class constant.
 */
public interface Components {

    String DEFAULT = "default";

    /**
     * EventDispatcher type.
     */
    interface EventDispatcher {

        String FLOW = "flow";

        String DISRUPTOR = "disruptor";

    }

    /**
     * ProxyFactory type.
     */
    interface ProxyFactory {

        String JDK = "jdk";

        String CGLIB = "cglib";

        String BYTEBUDDY = "byteBuddy";

    }

    /**
     * Protocol type.
     */
    interface Protocol {

        String VIRTUE = "virtue";

        String HTTP = "http";

        String HTTP1 = "http1.1";

    }

    /**
     * Registry type.
     */
    interface Registry {

        String ZOOKEEPER = "zookeeper";

        String CONSUL = "consul";

        String NACOS = "nacos";

        String REDIS = "redis";

    }

    /**
     * Serialization type.
     */
    interface Serialization {

        String JDK = "jdk";

        String JSON = "json";

        String FURY = "fury";

        String KRYO = "kryo";

        String MSGPACK = "msgpack";

        String PROTOBUF = "protobuf";

    }

    /**
     * Transport type.
     */
    interface Transport {

        String NETTY = "netty";

    }

    /**
     * FaultTolerance type.
     */
    interface FaultTolerance {

        String FAIL_RETRY = "failRetry";

        String FAIL_FAST = "failFast";

        String TIMEOUT_RETRY = "timeoutRetry";

    }

    /**
     * LoadBalance type.
     */
    interface LoadBalance {

        String RANDOM = "random";

        String ROUND_ROBIN = "roundRobin";

        String WEIGHTED_ROUND_ROBIN = "WeightedRoundRobin";

    }

    /**
     * ByteReader type.
     */
    interface ByteReader {
        String NIO_HEAP = "heap";
    }

    /**
     * ByteWriter type.
     */
    interface ByteWriter {
        String NIO_HEAP = "heap";
    }

    /**
     * Envelope type.
     */
    interface Envelope {

        String REQUEST = "request";

        String RESPONSE = "response";

        String HEARTBEAT = "heartbeat";

        String ERROR = "error";

    }

    /**
     * Compression type.
     */
    interface Compression {
        String GZIP = "gzip";
    }

}
