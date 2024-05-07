package io.virtue.rpc.virtue.envelope;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.serialization.Serializer;
import io.virtue.transport.compress.Compressor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * Virtue Envelope.
 */
@Getter
@Setter
@Accessors(fluent = true)
public abstract class VirtueEnvelope implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Object body;

    private URL url;

    protected VirtueEnvelope() {
    }

    protected VirtueEnvelope(URL url, Object body) {
        url(url);
        body(body);
    }

    /**
     * Get serializer.
     *
     * @return
     */
    public Serializer serializer() {
        return ExtensionLoader.loadExtension(Serializer.class, url.getParam(Key.SERIALIZATION));
    }

    /**
     * Get compression.
     *
     * @return
     */
    public Compressor compression() {
        return ExtensionLoader.loadExtension(Compressor.class, url.getParam(Key.COMPRESSION));
    }

}
