package io.virtue.transport.compress;

import io.virtue.common.exception.CompressionException;
import io.virtue.common.extension.spi.Extensible;

import static io.virtue.common.constant.Components.Compression.GZIP;

/**
 * Compression interface.
 */
@Extensible(GZIP)
public interface Compressor {

    /**
     * Compress date.
     *
     * @param data
     * @return
     * @throws CompressionException
     */
    byte[] compress(byte[] data) throws CompressionException;

    /**
     * Decompress compressedData.
     *
     * @param compressedData
     * @return
     * @throws CompressionException
     */
    byte[] decompress(byte[] compressedData) throws CompressionException;
}
