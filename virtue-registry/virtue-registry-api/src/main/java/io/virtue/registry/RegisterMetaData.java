package io.virtue.registry;

import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;

import java.util.Map;

/**
 * Register Meta.
 * <p>Registered Meta Data can be extended when you sign up for the service.</p>
 */
@Extensible(lazyLoad = false)
public interface RegisterMetaData {

    /**
     * The service is invoked before it is registered。
     *
     * @param url
     * @param metaData
     */
    void process(URL url, Map<String, String> metaData);
}
