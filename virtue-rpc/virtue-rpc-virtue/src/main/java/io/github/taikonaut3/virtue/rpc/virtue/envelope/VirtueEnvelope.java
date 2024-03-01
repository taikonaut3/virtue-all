package io.github.taikonaut3.virtue.rpc.virtue.envelope;

import io.github.taikonaut3.virtue.common.constant.Key;
import io.github.taikonaut3.virtue.common.spi.ExtensionLoader;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.serialization.Serializer;
import io.github.taikonaut3.virtue.transport.compress.Compression;
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
