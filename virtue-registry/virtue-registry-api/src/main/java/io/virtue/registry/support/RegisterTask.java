package io.virtue.registry.support;

import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.registry.RegisterMetaData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Register service task.
 */
@Getter
@Accessors(fluent = true)
public class RegisterTask implements Runnable {

    private static final List<RegisterMetaData> REGISTER_META_DATA = ExtensionLoader.loadExtensions(RegisterMetaData.class);

    private final URL url;

    private final BiConsumer<RegisterTask, Map<String, String>> task;

    @Setter
    private boolean isFirstRun = true;

    public RegisterTask(URL url, BiConsumer<RegisterTask, Map<String, String>> task) {
        this.url = url;
        this.task = task;
        run();
    }

    @Override
    public void run() {
        Map<String, String> metaData = new LinkedHashMap<>();
        REGISTER_META_DATA.forEach(registerMetaData -> registerMetaData.process(url, metaData));
        task.accept(this, metaData);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
