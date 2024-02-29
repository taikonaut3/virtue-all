package io.github.taikonaut3.virtue.rpc.http1.envelope;

import io.github.taikonaut3.virtue.common.constant.Components;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.common.util.AssertUtil;
import io.github.taikonaut3.virtue.rpc.http1.config.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 13:34
 */
@Accessors(fluent = true)
@Getter
public class HttpRequest extends HttpEnvelope {

    private final static String VERSION = Components.Protocol.HTTP1;

    private final URL url;

    private final HttpMethod method;

    public HttpRequest(HttpMethod method, URL url) {
        AssertUtil.notNull(method, url);
        this.method = method;
        this.url = url;
    }

}
