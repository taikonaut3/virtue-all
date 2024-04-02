package io.virtue.transport.compress;

import io.virtue.common.constant.Components;
import io.virtue.common.exception.CompressionException;
import io.virtue.common.spi.ServiceInterface;

/**
 * Compression interface.
 */
@ServiceInterface(Components.Compression.GZIP)
public interface Compression {

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
