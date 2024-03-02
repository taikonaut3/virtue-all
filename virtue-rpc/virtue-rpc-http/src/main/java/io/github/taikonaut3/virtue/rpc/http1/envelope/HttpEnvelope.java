package io.github.taikonaut3.virtue.rpc.http1.envelope;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 14:27
 */
public class HttpEnvelope {

    protected Map<String, String> headers;
    protected Object body;

    protected HttpEnvelope() {
        headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Object body() {
        return body;
    }

    public void body(Object body) {
        this.body = body;
    }

}
