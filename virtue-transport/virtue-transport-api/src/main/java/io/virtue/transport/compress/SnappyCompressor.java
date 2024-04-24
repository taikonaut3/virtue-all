package io.virtue.transport.compress;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Based on the snappy compression algorithm.
 */
public class SnappyCompressor extends AbstractCompressor {
    @Override
    protected byte[] doCompress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    @Override
    protected byte[] doDecompress(byte[] data) throws IOException {
        return Snappy.uncompress(data);
    }
}
