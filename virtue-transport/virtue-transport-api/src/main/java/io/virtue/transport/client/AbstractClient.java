package io.virtue.transport.client;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.endpoint.EndpointAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract Client.
 */
public abstract class AbstractClient extends EndpointAdapter implements Client {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    protected int connectTimeout;

    protected Channel channel;

    protected ChannelHandler channelHandler;

    protected Codec codec;

    protected URL url;

    protected boolean isInit = false;

    protected AbstractClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url.host(), url.port());
        this.url = url;
        this.channelHandler = channelHandler;
        this.codec = codec;
        this.connectTimeout = url.getIntParam(Key.CONNECT_TIMEOUT, Constant.DEFAULT_CONNECT_TIMEOUT);
        try {
            connect();
            if (logger.isDebugEnabled()) {
                logger.debug("Create <{}>{} succeeded,connect to remoteAddress: {}", url.protocol(), simpleClassName(this), address());
            }
        } catch (Throwable e) {
            throw new ConnectException(String.format("Create <%s>%s failed,connect to remoteAddress: %s", url.protocol(), simpleClassName(this), address()), e);
        }
    }

    @Override
    public void connect() throws ConnectException {
        if (isActive()) {
            return;
        }
        // When reconnecting, there is no need to initialize again
        if (!isInit) {
            doInit();
            isInit = true;
        }
        doConnect();
    }

    @Override
    public void close() throws NetWorkException {
        try {
            channel.close();
        } catch (Throwable e) {
            throw new NetWorkException(e);
        }
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public String toString() {
        return simpleClassName(this) + " connect to " + NetUtil.getAddress(inetSocketAddress());
    }

    protected abstract void doInit() throws ConnectException;

    protected abstract void doConnect() throws ConnectException;

}
