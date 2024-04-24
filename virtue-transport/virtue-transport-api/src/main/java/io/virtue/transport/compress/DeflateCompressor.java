package io.virtue.transport.compress;

import io.virtue.common.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Based on the deflate compression algorithm.
 */
public class DeflateCompressor extends AbstractCompressor {

    @Override
    protected byte[] doCompress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteOutput)) {
            deflaterOutputStream.write(data);
        }
        return byteOutput.toByteArray();
    }

    @Override
    protected byte[] doDecompress(byte[] data) throws IOException {
        try (InflaterInputStream inflaterInput= new InflaterInputStream(new ByteArrayInputStream(data))) {
            return FileUtil.inputStreamToByteArray(inflaterInput);
        }
    }
}
