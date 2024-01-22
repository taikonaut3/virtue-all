package io.github.astro.virtue.config.config;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.NetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/7 14:00
 */
@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class EventDispatcherConfig extends UrlTypeConfig {


    @Parameter(Key.BUFFER_SIZE)
    private int bufferSize = Constant.DEFAULT_BUFFER_SIZE;

    public EventDispatcherConfig() {
        type(Constant.DEFAULT_EVENT_DISPATCHER);
    }

    @Override
    public URL toUrl() {
        return new URL(type, NetUtil.getLocalHost(), 0, parameterization());
    }
}
