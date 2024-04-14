package io.virtue.rpc.h1.config;

import io.virtue.transport.RpcFuture;
import io.virtue.transport.http.h1.HttpRequest;

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
