package io.virtue.transport.compress;

import io.virtue.common.exception.CompressionException;

import java.io.IOException;

/**
 * Abstract Compressor.
 */
public abstract class AbstractCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] data) throws CompressionException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        try {
            return doCompress(data);
        } catch (IOException e) {
            throw new CompressionException(e);
        }
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws CompressionException {
        if (compressedData == null || compressedData.length == 0) {
            return new byte[0];
        }
        try {
            return doDecompress(compressedData);
        } catch (IOException e) {
            throw new CompressionException(e);
        }
    }

    protected abstract byte[] doCompress(byte[] data) throws IOException;

    protected abstract byte[] doDecompress(byte[] data) throws IOException;
}
