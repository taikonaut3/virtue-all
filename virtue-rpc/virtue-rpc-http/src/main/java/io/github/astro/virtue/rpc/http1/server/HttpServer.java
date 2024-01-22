package io.github.astro.virtue.rpc.http1.server;

import io.github.astro.virtue.common.exception.BindException;
import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.NetUtil;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.transport.channel.Channel;
import io.github.astro.virtue.transport.server.Server;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/18 11:01
 */
public class HttpServer implements Server {

    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final Router router;

    private io.vertx.core.http.HttpServer httpServer;

    private Vertx vertx;

    private URL url;

    public HttpServer(URL url) {
        this.url = url;
        Virtue virtue = Virtue.getDefault();
        vertx = (Vertx) virtue.getDataOrPut("vertx", Vertx.vertx());
        // 创建一个路由
        router = Router.router(vertx);

        // 挂载 Handler，接收所有请求路径和请求方法的请求
        router.route().handler(context -> {
            String s = context.request().absoluteURI();
            System.out.println("path" + context.request().path());
            System.out.println("uri" + s);
            // 获取请求的远程地址
            String address = context.request().connection().remoteAddress().toString();
            // 获取请求参数 "name" 的取值
            MultiMap queryParams = context.queryParams();
            String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
            // 写json响应
            context.json(
                    new JsonObject()
                            .put("name", name)
                            .put("address", address)
                            .put("message", "Hello " + name + " connected from " + address)
            );
        });
        bind();
    }

    @Override
    public void close() throws NetWorkException {
        httpServer.close();
    }

    @Override
    public boolean isActive() {
        return httpServer != null;
    }

    @Override
    public String host() {
        return NetUtil.getLocalHost();
    }

    @Override
    public int port() {
        return httpServer.actualPort();
    }

    @Override
    public InetSocketAddress toInetSocketAddress() {
        return null;
    }

    @Override
    public void bind() throws BindException {
        vertx.createHttpServer()
                // 使用路由处理所有请求
                .requestHandler(router)
                // 开始监听端口
                .listen(url.port())
                // 打印监听的端口
                .onSuccess(server -> {
                            httpServer = server;
                            logger.info("HTTP server started on port " + server.actualPort());
                        }
                );
    }

    @Override
    public Channel[] channels() {
        return new Channel[0];
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return null;
    }
}
