package io.virtue.core.config;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * EventDispatcher Config.
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
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
