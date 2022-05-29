package top.parak.kraft.kvstore.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.Node;
import top.parak.kraft.kvstore.support.toolkit.RuntimeUtil;

import java.io.IOException;

/**
 * KV-store server.
 *
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class KVStoreServer {

    private static final Logger logger = LoggerFactory.getLogger(KVStoreServer.class);

    private final Node node;
    private final int port;
    private final KVStoreServerHandler serverHandler;

    /**
     * Create KVStoreServer.
     *
     * @param node node
     * @param port port
     */
    public KVStoreServer(Node node, int port) {
        this.node = node;
        this.port = port;
        this.serverHandler = new KVStoreServerHandler(new KVStoreServerService(node));
    }

    /**
     * Boss group.
     */
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * Worker group.
     */
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup(2 * RuntimeUtil.cpus());
    /**
     * Handler group.
     */
    private final NioEventLoopGroup handlerGroup = new NioEventLoopGroup();

    /**
     * Start server.
     *
     * @throws IOException if IO exception occurs
     */
    public void start() throws IOException {
        this.node.start();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)  // use nagle algorithm
                .childOption(ChannelOption.SO_KEEPALIVE, true) // open tcp heart beat
                .option(ChannelOption.SO_BACKLOG, 128)         // length of connection queue
                .handler(new LoggingHandler(LogLevel.DEBUG))         // netty log level
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new KVStoreMessageEncoder());
                        p.addLast(new KVStoreMessageDecoder());
                        p.addLast(handlerGroup, serverHandler);
                    }
                });
        logger.info("netty rpc server started at port {}", this.port);
        serverBootstrap.bind(this.port);
    }

    /**
     * Stop server.
     *
     * @throws InterruptedException if interrupted
     * @throws IOException if IO exception occurs
     */
    public void stop() throws Exception {
        logger.info("stopping rpc server ...");
        this.node.stop();
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
        this.handlerGroup.shutdownGracefully();
    }

}
