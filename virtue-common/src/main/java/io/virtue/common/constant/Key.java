package io.virtue.common.constant;

import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Key constant.
 */
public interface Key {

    String UNIQUE_ID = "uniqueId";

    String APPLICATION = "application";

    String SERVICE = "service";

    String PROTOCOL = "protocol";

    String EVENT_DISPATCHER = "eventDispatcher";

    String BUFFER_SIZE = "bufferSize";

    String SERVER_EVENT_DISPATCHER = "serverEventDispatcher";

    String PROTOCOL_VERSION = "protocolVersion";

    String CLASS = "class";

    String BODY_TYPE = "bodyType";

    String REGISTERS = "registers";

    String ONEWAY = "oneway";

    String RETRY_INTERVAL = "retryInterval";

    String HEALTH_CHECK_INTERVAL = "healthCheckInterval";

    String ENABLED = "enabled";

    String HTTP2_TRANSPORT = "h2_transport";

    String VERSION = "version";

    String GROUP = "group";

    String WEIGHT = "weight";

    String MSG_TYPE = "msgType";

    String TIMEOUT = "timeout";

    String KEEP_ALIVE_TIMEOUT = "keepAliveTimeout";

    String SPARE_CLOSE_TIMES = "spareCloseTimes";

    String HEARTBEAT_LOG_ENABLE = "heartbeatLogEnable";

    String SO_BACKLOG = "soBacklog";

    String MAX_CONNECTIONS = "maxConnections";

    String MAX_THREADS = "maxThreads";

    String ASYNC = "async";

    String LAZY_DISCOVER = "lazyDiscover";

    String PROXY = "proxy";

    String TRANSPORTER = "transporter";

    String DISCOVER_URL = "discoverUrl";

    String REQUEST_CONTEXT = "requestContext";

    String RESPONSE_CONTEXT = "responseContext";

    String MULTIPLEX = "multiplex";

    String SERIALIZATION = "serialization";

    String LOAD_BALANCE = "loadBalance";

    String SERVICE_DISCOVERY = "serviceDiscovery";

    String ROUTER = "router";

    String RETRIES = "retries";

    String REQUEST = "request";

    String RESPONSE = "response";

    String FAULT_TOLERANCE = "faultTolerance";

    String CHECK = "check";

    String TIMESTAMP = "timestamp";

    String DYNAMIC = "dynamic";

    String SUBSCRIBE = "subscribe";

    String MAX_RECEIVE_SIZE = "maxReceiveSize";

    String CLIENT_MAX_RECEIVE_SIZE = "clientMaxReceiveSize";

    String MAX_HEADER_SIZE = "maxHeaderSize";

    String SSL = "ssl";

    String CONNECT_TIMEOUT = "connectTimeout";

    String COMPRESSION = "compression";

    String KEEPALIVE = "keepAlive";

    String GLOBAL = "global";

    String URL = "url";

    String VIRTUE_URL = "virtue-url";

    String HTTP_REQUEST_WRAPPER = "httpRequestWrapper";

    String ENVELOPE = "envelope";
    String RESPONSE_CODE = "responseCode";

    String INVOKER = "invoker";

    String RETURN_TYPE = "returnType";

    String PASSWORD = "password";

    String ENABLE_HEALTH_CHECK = "enableHealthCheck";

    String USERNAME = "username";

    String SESSION_TIMEOUT = "sessionTimeout";

    String INTERVAL = "interval";

    String DESCRIPTION = "description";

    String CLIENT = "client";
    String FUTURE_FACTORY = "futureFactory";
    String HTTP_METHOD = "httpMethod";

    String LOCAL_VIRTUE = "localVirtue";

    String CLIENT_VIRTUE = "clientVirtue";

    String SERVER_VIRTUE = "serverVirtue";
    AttributeKey<AtomicInteger> ALL_IDLE_TIMES = AttributeKey.of("allIdleTimes");
    AttributeKey<AtomicInteger> WRITE_IDLE_TIMES = AttributeKey.of("writeIdleTimes");
    AttributeKey<AtomicInteger> READER_IDLE_TIMES = AttributeKey.of("readeIdleTimes");
    AttributeKey<AtomicInteger> LAST_CALL_INDEX = AttributeKey.of("lastCallIndex");
    AttributeKey<Throwable> CALL_EXCEPTION = AttributeKey.of("callException");

    AttributeKey<URL> HTTP_URL = AttributeKey.of("httpUrl");

    String VERTX = "vertx";
    String SUBSCRIBES = "subscribes";

    String CA_PATH = "virtue.h2.ca.path";

    String CLIENT_CERT_PATH = "virtue.http.client.cert.path";

    String CLIENT_KEY_PATH = "virtue.http.client.key.path";

    String SERVER_CERT_PATH = "virtue.http.server.cert.path";

    String SERVER_KEY_PATH = "virtue.http.server.key.path";
}
