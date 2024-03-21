package io.virtue.transport.client;

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

public abstract class AbstractClient extends EndpointAdapter implements Client {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    public int connectTimeout;

    protected Channel channel;

    protected ChannelHandler channelHandler;

    protected Codec codec;

    protected URL url;

    private boolean isInit = false;

    protected AbstractClient(URL url, ChannelHandler channelHandler, Codec codec) throws ConnectException {
        super(url.host(), url.port());
        this.url = url;
        this.channelHandler = channelHandler;
        this.codec = codec;
        try {
            connect();
            logger.debug("Create {} is Successful,Connect to remoteAddress: {} for Protocol({})",
                    this.getClass().getSimpleName(), address(), url.protocol());
        } catch (Throwable e) {
            logger.error("Create Client is Failed,Connect to remoteAddress:" + address(), e);
            throw new ConnectException("Create Client is Failed,Connect to remoteAddress :" + address(), e);
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
        } finally {
            doClose();
        }
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " connect to " + NetUtil.getAddress(toInetSocketAddress());
    }

    protected abstract void doInit() throws ConnectException;

    protected abstract void doConnect() throws ConnectException;

    protected abstract void doClose() throws NetWorkException;

}
