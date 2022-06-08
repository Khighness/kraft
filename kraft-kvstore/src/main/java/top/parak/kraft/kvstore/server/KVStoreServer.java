package top.parak.kraft.kvstore.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

    /**
     * Raft node.
     */
    private final Node node;
    /**
     * Service port.
     */
    private final int port;
    /**
     * KV-store service.
     */
    private final KVStoreServerService KVStoreServerService;
    /**
     * Netty boss group., default 1 thread.
     */
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * Netty worker group, default (2 * cpu) thread.
     */
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup(2 * RuntimeUtil.cpus());
    /**
     * Netty handler group.
     */
    private final NioEventLoopGroup handlerGroup = new NioEventLoopGroup();

    /**
     * Create KVStoreServer.
     *
     * @param node raft node
     * @param port service port
     */
    public KVStoreServer(Node node, int port) {
        this.node = node;
        this.KVStoreServerService = new KVStoreServerService(node);
        this.port = port;
    }

    /**
     * Start server.
     *
     * @throws IOException if IO exception occurs
     */
    public void start() throws Exception {
        this.node.start();

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)  // use nagle algorithm
                .childOption(ChannelOption.SO_KEEPALIVE, true) // open tcp heart beat
                .option(ChannelOption.SO_BACKLOG, 128)         // length of connection queue
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new KVStoreMessageEncoder());
                        pipeline.addLast(new KVStoreMessageDecoder());
                        pipeline.addLast(handlerGroup, new KVStoreServerHandler(KVStoreServerService));
                    }
                });
        logger.info("kv-store server is serving at port {}", this.port);
        serverBootstrap.bind(this.port);
    }

    /**
     * Stop server.
     *
     * @throws InterruptedException if interrupted
     * @throws IOException if IO exception occurs
     */
    public void stop() throws Exception {
        logger.info("stopping kv-store server...");
        this.node.stop();
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
    }

}
