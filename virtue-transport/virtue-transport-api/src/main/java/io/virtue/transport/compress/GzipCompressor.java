package io.virtue.transport.compress;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static io.virtue.common.constant.Components.Compression.GZIP;

/**
 * Based on the gzip compression algorithm.
 */
@Extension(GZIP)
public class GzipCompressor extends AbstractCompressor {

    @Override
    protected byte[] doCompress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput)) {
            gzipOutput.write(data);
        }
        return byteOutput.toByteArray();
    }

    @Override
    protected byte[] doDecompress(byte[] data) throws IOException {
        try (GZIPInputStream gzipInput = new GZIPInputStream(new ByteArrayInputStream(data))) {
            return FileUtil.inputStreamToByteArray(gzipInput);
        }
    }
}
