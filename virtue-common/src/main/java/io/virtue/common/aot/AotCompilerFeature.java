package io.virtue.common.aot;

import io.virtue.common.extension.spi.ExtensionLoader;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.util.List;

public class AotCompilerFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        RuntimeReflection.register(AotCompiler.class);
        RuntimeReflection.register(ExtensionLoader.class);
        RuntimeReflection.registerClassLookup("io.virtue.rpc.support.reflect.AccessClassLoader");
        RuntimeReflection.registerClassLookup("io.virtue.rpc.support.reflect.MethodAccess");
        RuntimeReflection.registerClassLookup("io.github.taikonaut3.ProviderMethodAccess");
        List<AotCompiler> compilers = ExtensionLoader.loadExtensions(AotCompiler.class);
        compilers.forEach(AotCompiler::process);
        AotCompiler.METAS.forEach(ReflectMeta::register);
    }
}
