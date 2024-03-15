package io.virtue.common.extension;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;

import java.util.Map;

public class RpcContext extends AbstractAccessor {

    private static final ThreadLocal<CurrentContext> CURRENT_CONTEXT = ThreadLocal.withInitial(CurrentContext::new);
    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT = ThreadLocal.withInitial(RequestContext::new);
    private static final ThreadLocal<ResponseContext> RESPONSE_CONTEXT = ThreadLocal.withInitial(ResponseContext::new);

    public static RequestContext requestContext() {
        return REQUEST_CONTEXT.get();
    }

    public static ResponseContext responseContext() {
        return RESPONSE_CONTEXT.get();
    }

    public static CurrentContext currentContext() {
        return CURRENT_CONTEXT.get();
    }

    public static void clear() {
        currentContext().clear();
        requestContext().clear();
        responseContext().clear();

    }
    public RpcContext remove(String key) {
        super.remove(key);
        return this;
    }

    public static class CurrentContext extends AbstractAccessor {
        public CurrentContext remove(String key) {
            super.remove(key);
            return this;
        }

        public void clear() {
            accessor.clear();
            CURRENT_CONTEXT.remove();
        }
    }

    public static class RequestContext extends StringAccessor<RequestContext> {
        public void clear() {
            accessor.clear();
            REQUEST_CONTEXT.remove();
        }

        @Override
        public String toString() {
            return URL.mapToUrlString(accessor);
        }

        public static void parse(URL url) {
            String requestContextStr = url.getParameter(Key.REQUEST_CONTEXT);
            if (!StringUtil.isBlank(requestContextStr)) {
                for (Map.Entry<String, String> entry : URL.urlStringToMap(requestContextStr).entrySet()) {
                    requestContext().set(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static class ResponseContext extends StringAccessor<ResponseContext> {
        public void clear() {
            accessor.clear();
            RESPONSE_CONTEXT.remove();
        }

        @Override
        public String toString() {
            return  URL.mapToUrlString(accessor);
        }

        public static void parse(URL url) {
            String responseContextStr = url.getParameter(Key.RESPONSE_CONTEXT);
            if (!StringUtil.isBlank(responseContextStr)) {
                for (Map.Entry<String, String> entry : URL.urlStringToMap(responseContextStr).entrySet()) {
                    responseContext().set(entry.getKey(), entry.getValue());
                }
            }
        }
    }

}
