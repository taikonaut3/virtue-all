package io.virtue.rpc.h2.parser;

import io.virtue.core.Invocation;
import io.virtue.rpc.h2.Http2Invocation;

import java.lang.reflect.Method;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/23 10:41
 */
public class JaxRsMethodParser implements MethodParser{
    @Override
    public void parseMethod(Invocation invocation) {
        Http2Invocation http2Invocation = (Http2Invocation) invocation;
        Method method = http2Invocation.invoker().method();
        Object[] args = http2Invocation.args();
    }
}
