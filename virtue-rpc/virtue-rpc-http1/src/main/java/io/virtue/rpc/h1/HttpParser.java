package io.virtue.rpc.h1;

import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpParser
 */
@Data
@Accessors(fluent = true)
public class HttpParser implements ProtocolParser {

    public Map<String, String> parseHeaders(String[] headers) {
        return getStringMap(headers);
    }

    public Map<String, String> parseParams(String[] params) {
        return getStringMap(params);
    }

    private Map<String, String> getStringMap(String[] params) {
        if (params == null || params.length == 0) {
            return new HashMap<>();
        }
        return Arrays.stream(params)
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0].trim(),
                        keyValue -> keyValue[1].trim()
                ));
    }

    @Override
    public Invocation parseOfRequest(Request request) {
        return null;
    }

    @Override
    public Object parseOfResponse(Response response) {
        return null;
    }
}
