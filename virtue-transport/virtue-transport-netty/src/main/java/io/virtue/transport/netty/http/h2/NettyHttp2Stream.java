package io.virtue.transport.netty.http.h2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.virtue.common.url.URL;
import io.virtue.common.util.bytes.ByteWriter;
import io.virtue.common.util.bytes.HeapByteWriter;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.http.h2.Http2Envelope;
import io.virtue.transport.netty.NettySupport;

import java.util.Map;

/**
 * Handle h2 frames,Then generate Full Http2 stream message.
 */
public class NettyHttp2Stream implements Http2Envelope {

    private final URL url;

    private final int streamId;

    private final NettyHttp2Headers headers;

    private ByteWriter byteWriter;

    private boolean endStream;

    public NettyHttp2Stream(URL url, int streamId) {
        this.url = url;
        this.streamId = streamId;
        headers = new NettyHttp2Headers();
        endStream = false;
    }

    /**
     * Parse header frame.
     *
     * @param headersFrame
     */
    public void parseHeaderFrame(Http2HeadersFrame headersFrame) {
        NettyHttp2Headers headers = new NettyHttp2Headers(headersFrame.headers());
        addHeaders(headers);
        if (headersFrame.isEndStream()) {
            end();
        }
    }

    /**
     * Parse data frame.
     *
     * @param dataFrame
     */
    public void parseDataFrame(Http2DataFrame dataFrame) {
        ByteBuf byteBuf = dataFrame.content();
        byte[] bytes = NettySupport.getBytes(byteBuf);
        writeData(bytes);
        if (dataFrame.isEndStream()) {
            end();
        }
    }

    @Override
    public void addHeaders(HttpHeaders headers) {
        this.headers.add(headers);
    }

    @Override
    public void writeData(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
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
     * Check whether the current HTTP 2 stream has been read.
     *
     * @return
     */
    public boolean endStream() {
        return endStream;
    }

    /**
     * Get netty http2 headers.
     *
     * @return
     */
    public Http2Headers http2Headers() {
        return headers.headers();
    }

    /**
     * The current stream read has ended.
     */
    private void end() {
        this.endStream = true;
        CharSequence pathAndParams = headers.headers().path();
        url.set(HttpMethod.ATTRIBUTE_KEY, method());
        if (pathAndParams != null) {
            // server endpoint
            String paramsString = pathAndParams.toString();
            String path = URL.parsePath(paramsString);
            url.addPaths(URL.pathToList(path));
            Map<CharSequence, CharSequence> params = URL.parseParams(paramsString);
            if (params != null) {
                params.forEach((k, v) -> url.addParam(k.toString(), v.toString()));
            }
        }
    }
}
