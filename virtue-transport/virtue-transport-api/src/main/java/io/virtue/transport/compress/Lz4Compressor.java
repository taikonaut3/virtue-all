package io.virtue.transport.compress;

import io.virtue.common.util.FileUtil;
import net.jpountz.lz4.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Based on the lz4 compression algorithm.
 */
public class Lz4Compressor extends AbstractCompressor {

    private final LZ4Compressor compressor;

    private final LZ4FastDecompressor decompressor;

    public Lz4Compressor() {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        compressor = factory.fastCompressor();
        decompressor = factory.fastDecompressor();
    }

    @Override
    protected byte[] doCompress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try (LZ4BlockOutputStream lz4Output = new LZ4BlockOutputStream(byteOutput, 1024, compressor)) {
            lz4Output.write(data);
        }
        return byteOutput.toByteArray();
    }

    @Override
    protected byte[] doDecompress(byte[] data) throws IOException {
        try (LZ4BlockInputStream lz4Input = new LZ4BlockInputStream(new ByteArrayInputStream(data), decompressor)) {
            return FileUtil.inputStreamToByteArray(lz4Input);
        }
    }
}
