package io.virtue.transport.server;

import io.virtue.common.exception.BindException;
import io.virtue.common.exception.NetWorkException;
import io.virtue.common.url.URL;
import io.virtue.common.util.NetUtil;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.endpoint.EndpointAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract server.
 */
public abstract class AbstractServer extends EndpointAdapter implements Server {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    protected int soBacklog;

    protected ChannelHandler channelHandler;

    protected Codec codec;

    protected URL url;

    private boolean isInit = false;

    protected AbstractServer(URL url, ChannelHandler channelHandler, Codec codec) throws BindException {
        super(url.host(), url.port());
        this.url = url;
        this.channelHandler = channelHandler;
        this.codec = codec;
        try {
            bind();
            if (logger.isDebugEnabled()) {
                logger.debug("Create <{}>{} succeeded,bind port(s): {}", url.protocol(), simpleClassName(this), port());
            }
        } catch (Throwable e) {
            throw new BindException(String.format("Create <%s>%s succeeded,bind port(s): %s", url.protocol(), simpleClassName(this), port()), e);
        }
    }

    @Override
    public void bind() throws BindException {
        if (isActive()) {
            return;
        }
        if (!isInit) {
            doInit();
            isInit = true;
        }
        doBind();
    }

    @Override
    public void close() {
        for (Channel channel : channels()) {
            try {
                channel.close();
            } catch (Throwable e) {
                throw new NetWorkException(e);
            }
        }
        try {
            doClose();
        } catch (Throwable e) {
            throw new NetWorkException(e);
        }
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        for (Channel channel : channels()) {
            if (NetUtil.isSameAddress(channel.remoteAddress(), remoteAddress)) {
                return channel;
            }
        }
        return null;
    }

    protected abstract void doInit() throws BindException;

    protected abstract void doBind() throws BindException;

    protected abstract void doClose() throws NetWorkException;

}
