package http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.rpc.handler.BaseClientChannelHandlerChain;
import io.virtue.transport.Request;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandlerAdapter;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.http.h2.Http2Transporter;
import io.virtue.transport.netty.http2.NettyHttp2Transporter;
import io.virtue.transport.netty.http2.client.Http2Client;
import io.virtue.transport.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/7 14:16
 */
public class Http2Test {

    @Test
    public void startServer() throws IOException {
        Http2Transporter transporter = new NettyHttp2Transporter();

        URL url = new ServerConfig("http2", 8082).toUrl();
        url.addParam(Key.SSL, "true");
        ChannelHandlerAdapter handlerAdapter = new ChannelHandlerAdapter() {
            @Override
            public void received(Channel channel, Object message) throws RpcException {
                super.received(channel, message);
                if (message instanceof Request request && request.message() instanceof Http2Request streamEnvelope) {
                    ByteBuf data = unreleasableBuffer(copiedBuffer("Server Hello World ", CharsetUtil.UTF_8)).asReadOnly();
                    byte[] bytes = new byte[data.readableBytes()];
                    data.readBytes(bytes);
                    HashMap<CharSequence, CharSequence> headers = new HashMap<>();
                    headers.put("zzz", "123");
                    streamEnvelope.url().set(HttpMethod.ATTRIBUTE_KEY,streamEnvelope.method());
                    Http2Response http2Response = transporter.newResponse(200, streamEnvelope.url(), headers, bytes);
                }
            }
        };
        Server server = transporter.bind(url, handlerAdapter, null);
        System.in.read();
    }

    @Test
    public void startClient() throws IOException {
        URL url = new ClientConfig("h2").toUrl();
        url.addParam(Key.SSL, "true");
        url.port(8082);
        Http2Client http2Client = new Http2Client(url, new BaseClientChannelHandlerChain(), null);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        http2Client.send("");
//        for (int i = 0; i < 3; i++) {
//            int finalI = i;
//            executorService.execute(() -> {
//                final DefaultHttp2Headers headers = new DefaultHttp2Headers();
//                headers.method("GET");
//                headers.path("/test" + finalI);
//                headers.scheme("https");
//                final Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, false);
//                ByteBuf data = unreleasableBuffer(copiedBuffer("Hello World " + finalI, CharsetUtil.UTF_8)).asReadOnly();
//                DefaultHttp2DataFrame dataFrame = new DefaultHttp2DataFrame(data, true);
//                http2Client.send(headersFrame, dataFrame);
//            });
//
//        }

        System.out.println(http2Client);
        System.in.read();
    }
}
