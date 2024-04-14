package io.virtue.transport.http.h1;

import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;

/**
 * Http Envelope.
 */
public interface HttpEnvelope {

    /**
     * Http version.
     *
     * @return
     */
    HttpVersion version();

    /**
     * Http method.
     *
     * @return
     */
    HttpMethod method();

    /**
     * Http url.
     *
     * @return
     */
    URL url();

    /**
     * Http headers.
     *
     * @return
     */
    HttpHeaders headers();

    /**
     * Http Body data.
     *
     * @return
     */
    byte[] data();
}
