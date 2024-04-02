package io.virtue.rpc.http1.config;

import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.http1.envelope.HttpRequest;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 14:50
 */
public interface HttpClient {

    RpcFuture send(HttpRequest request);

//    @Override
//    default void close() throws NetWorkException {
//
//    }
//
//    @Override
//    default boolean isActive() {
//
//    }
//
//    @Override
//    default void connect() throws ConnectException {
//
//    }
//
//    @Override
//    default Channel channel() {
//
//    }
//
//    @Override
//    default String host() {
//
//    }
//
//    @Override
//    default int port() {
//
//    }
//
//    @Override
//    default InetSocketAddress toInetSocketAddress() {
//
//    }
//
//    @Override
//    default String address() {
//
//    }
}
