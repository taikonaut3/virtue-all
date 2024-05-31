package io.virtue.common.aot;

import io.virtue.common.extension.spi.Extensible;

import java.util.LinkedList;
import java.util.List;

@Extensible
public interface AotCompiler {

    List<ReflectMeta> METAS = new LinkedList<>();

    void process();

    default AotCompiler register(ReflectMeta meta) {
        METAS.add(meta);
        return this;
    }
}
