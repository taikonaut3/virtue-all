package io.virtue.rpc.virtue.envelope;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.serialization.Serializer;
import io.virtue.transport.compress.Compression;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

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

    public Serializer serializer() {
        return ExtensionLoader.loadService(Serializer.class, url.getParameter(Key.SERIALIZE));
    }

    public Compression compression() {
        return ExtensionLoader.loadService(Compression.class, url.getParameter(Key.COMPRESSION));
    }

}
