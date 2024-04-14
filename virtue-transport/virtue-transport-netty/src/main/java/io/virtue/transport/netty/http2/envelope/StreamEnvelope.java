package io.virtue.transport.netty.http2.envelope;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.url.URL;
import io.virtue.common.util.bytes.ByteWriter;
import io.virtue.common.util.bytes.HeapByteWriter;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.http.h2.Http2Envelope;

/**
 * Http2 stream message.
 */
public class StreamEnvelope implements Http2Envelope {

    private final URL url;

    private final int streamId;

    private final NettyHttp2Headers headers;

    private ByteWriter byteWriter;

    private boolean endStream;

    public StreamEnvelope(URL url, int streamId) {
        this.url = url;
        this.streamId = streamId;
        headers = new NettyHttp2Headers();
        endStream = false;
    }

    @Override
    public void addHeaders(HttpHeaders headers) {
        this.headers.add(headers);
    }

    @Override
    public void writeData(byte[] data) {
        if (byteWriter == null) {
            CharSequence contentLength = headers.get(HttpHeaderNames.CONTENT_LENGTH);
            int capacity = data.length;
            if (contentLength != null) {
                capacity = Integer.parseInt(contentLength.toString());
            }
            byteWriter = new HeapByteWriter(capacity);
        }
        byteWriter.writeBytes(data);
    }

    @Override
    public int streamId() {
        return streamId;
    }

    @Override
    public HttpVersion version() {
        return HttpVersion.HTTP_2_0;
    }

    @Override
    public HttpMethod method() {
        CharSequence method = headers.headers().method();
        if (method == null) {
            return null;
        }
        return HttpMethod.valueOf(method.toString());
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public byte[] data() {
        if (!endStream) {
            throw new UnsupportedOperationException("Stream is not end");
        }
        return byteWriter == null ? new byte[0] : byteWriter.toBytes();
    }

    /**
     * The current stream read has ended.
     */
    public StreamEnvelope end() {
        this.endStream = true;
        CharSequence path = headers.headers().path();
        if (headers.headers().path() != null) {
            url.addPath(path.toString());
        }
        return this;
    }
}
