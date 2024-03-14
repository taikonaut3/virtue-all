package io.virtue.rpc.objectpool;

import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * ClientFactory
 */
public class ClientFactory implements PooledObjectFactory<Client> {

    private final Transporter transporter;

    private final Codec codec;

    private final ChannelHandler handler;

    public ClientFactory(Transporter transporter, Codec codec, ChannelHandler handler) {
        this.codec = codec;
        this.handler = handler;
        this.transporter = transporter;
    }

    @Override
    public void activateObject(PooledObject<Client> pooledObject) throws Exception {
        Client client = pooledObject.getObject();
        if (!client.isActive()) {
            client.connect();
        }
    }

    @Override
    public void destroyObject(PooledObject<Client> pooledObject) throws Exception {
        Client client = pooledObject.getObject();
        client.close();
    }

    @Override
    public PooledObject<Client> makeObject() throws Exception {
        URL url = RpcContext.getContext().attribute(URL.ATTRIBUTE_KEY).get();
        Client client = transporter.connect(url, handler, codec);
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void passivateObject(PooledObject<Client> pooledObject) throws Exception {
        Client client = pooledObject.getObject();
        client.channel().close();
    }

    @Override
    public boolean validateObject(PooledObject<Client> pooledObject) {
        Client client = pooledObject.getObject();
        return client.isActive();
    }
}
