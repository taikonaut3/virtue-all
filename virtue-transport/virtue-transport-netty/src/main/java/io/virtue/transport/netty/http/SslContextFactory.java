package io.virtue.transport.netty.http;

import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ResourceException;
import io.virtue.common.exception.RpcException;

import javax.net.ssl.SSLException;

import static io.virtue.transport.netty.NettySupport.getSslBytes;
import static io.virtue.transport.netty.NettySupport.readBytes;

/**
 * SslContext Factory.
 */
public class SslContextFactory {

    private static final byte[] CA_BYTES;
    private static final byte[] SERVER_CERT_BYTES;
    private static final byte[] SERVER_KEY_BYTES;
    private static final byte[] CLIENT_CERT_BYTES;
    private static final byte[] CLIENT_KEY_BYTES;

    static {
        try {
            CA_BYTES = getSslBytes(Key.CA_PATH, Constant.INTERNAL_CERTS_PATH + "ca-cert.pem");
            SERVER_CERT_BYTES = getSslBytes(Key.SERVER_CERT_PATH, Constant.INTERNAL_CERTS_PATH + "server-cert.pem");
            SERVER_KEY_BYTES = getSslBytes(Key.SERVER_KEY_PATH, Constant.INTERNAL_CERTS_PATH + "server-pkcs8-key.pem");
            CLIENT_CERT_BYTES = getSslBytes(Key.CLIENT_CERT_PATH, Constant.INTERNAL_CERTS_PATH + "client-cert.pem");
            CLIENT_KEY_BYTES = getSslBytes(Key.CLIENT_KEY_PATH, Constant.INTERNAL_CERTS_PATH + "client-pkcs8-key.pem");
        } catch (Exception e) {
            throw new ResourceException("Get ssl config exception", e);
        }
    }

    /**
     * Create ssl context for server.
     *
     * @param supportedProtocols
     * @return
     */
    public static SslContext createForServer(String... supportedProtocols) {
        SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
        //SelfSignedCertificate ssc = new SelfSignedCertificate();
        try {
            return SslContextBuilder.forServer(readBytes(SERVER_CERT_BYTES), readBytes(SERVER_KEY_BYTES))
                    .sslProvider(provider)
                    /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                     * Please refer to the HTTP/2 specification for cipher requirements. */
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .trustManager(readBytes(CA_BYTES))
                    .applicationProtocolConfig(
                            new ApplicationProtocolConfig(
                                    ApplicationProtocolConfig.Protocol.ALPN,
                                    // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                                    ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                    // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                    supportedProtocols)
                    ).build();
        } catch (SSLException e) {
            throw RpcException.unwrap(e);
        }
    }

    /**
     * Create ssl context for client.
     *
     * @param supportedProtocols
     * @return
     */
    public static SslContext createForClient(String... supportedProtocols) {
        SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
        try {
            return SslContextBuilder.forClient()
                    .sslProvider(provider)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .keyManager(readBytes(CLIENT_CERT_BYTES), readBytes(CLIENT_KEY_BYTES))
                    // you probably won't want to use this in production, but it is fine for this example:
                    .trustManager(readBytes(CA_BYTES))
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            supportedProtocols))
                    .build();
        } catch (SSLException e) {
            throw RpcException.unwrap(e);
        }
    }
}
