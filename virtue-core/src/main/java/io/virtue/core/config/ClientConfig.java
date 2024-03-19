package io.virtue.core.config;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
@AllArgsConstructor
public class ClientConfig extends UrlTypeConfig {

    @Parameter(Key.SSL)
    private boolean ssl = true;

    @Parameter(Key.CLIENT_MAX_RECEIVE_SIZE)
    private int maxMessageSize = Constant.DEFAULT_MAX_MESSAGE_SIZE;

    @Parameter(Key.CONNECT_TIMEOUT)
    private int connectTimeout = Constant.DEFAULT_CONNECT_TIMEOUT;

    @Parameter(Key.KEEP_ALIVE_TIMEOUT)
    private int keepAliveTimeout = Constant.DEFAULT_KEEP_ALIVE_TIMEOUT;

    @Parameter(Key.SO_BACKLOG)
    private int soBacklog = Constant.DEFAULT_SO_BACKLOG;

    @Parameter(Key.SPARE_CLOSE_TIMES)
    private int spareCloseTimes = Constant.DEFAULT_SPARE_CLOSE_TIMES;

    @Parameter(Key.KEEPALIVE)
    private boolean keepAlive = true;

    public ClientConfig(String type) {
        type(type);
        name(type);
    }

    @Override
    public URL toUrl() {
        return new URL(type, NetUtil.getLocalHost(), port, parameterization());
    }

}
