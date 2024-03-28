package io.virtue.core.manager;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.core.config.ServerConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ServerConfig Manager.
 */
public class ServerConfigManager extends AbstractManager<ServerConfig> {

    private final List<String> neededOpenProtocols = new LinkedList<>();

    public ServerConfigManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Register a ServerConfig. The protocol type is unique.
     *
     * @param config
     */
    public void register(ServerConfig config) {
        register(config.type(), config);
    }

    /**
     * Add a protocol type that needs to be opened.
     *
     * @param protocol
     */
    public void neededOpenProtocol(String protocol) {
        if (!neededOpenProtocols.contains(protocol)) {
            neededOpenProtocols.add(protocol);
        }
    }

    /**
     * By added the protocol type, get the ServerConfig and Open Server.
     *
     * @return
     */
    public List<URL> neededOpenServer() {
        ArrayList<URL> urls = new ArrayList<>();
        for (String neededOpenProtocol : neededOpenProtocols) {
            ServerConfig serverConfig = get(neededOpenProtocol);
            if (serverConfig != null) {
                URL url = serverConfig.toUrl();
                url.attribute(Virtue.ATTRIBUTE_KEY).set(virtue);
                url.addParam(Key.VIRTUE, virtue.name());
                String applicationName = virtue.applicationName();
                if (!StringUtil.isBlank(applicationName)) {
                    url.addParam(Key.APPLICATION, applicationName);
                }
                urls.add(url);
            }
        }
        return urls;
    }
}
