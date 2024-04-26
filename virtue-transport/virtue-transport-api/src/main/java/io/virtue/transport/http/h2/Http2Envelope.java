package io.virtue.transport.http.h2;

import io.virtue.transport.http.h1.HttpEnvelope;
import io.virtue.transport.http.h1.HttpHeaders;

/**
 * Http2 envelope,then it may not be complete.
 */
public interface Http2Envelope extends HttpEnvelope {

    /**
     * Add headers.
     *
     * @param headers
     */
    void addHeaders(HttpHeaders headers);

    /**
     * Write data from data frame.
     *
     * @param data
     */
    void writeData(byte[] data);

    /**
     * Http2 stream id.
     *
     * @return
     */
    int streamId();
}
