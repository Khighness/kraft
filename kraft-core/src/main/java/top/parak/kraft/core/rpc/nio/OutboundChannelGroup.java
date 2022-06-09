package top.parak.kraft.core.rpc.nio;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;
import top.parak.kraft.core.rpc.ChannelConnectException;
import top.parak.kraft.core.rpc.ChannelException;

import javax.annotation.concurrent.ThreadSafe;
import java.net.ConnectException;
import java.util.concurrent.*;

/**
 * The container to manage outbound channels (channels to remote channels).
 *
 * @author KHighness
 * @since 2022-05-25
 * @email parakovo@gmail.com
 */
@ThreadSafe
class OutboundChannelGroup {

    private static final Logger logger = LoggerFactory.getLogger(OutboundChannelGroup.class);
    private final EventLoopGroup workerGroup;
    private final EventBus eventBus;
    private final NodeId slefId;
    private final int connectTimeoutMillis;
    private final ConcurrentMap<NodeId, Future<NioChannel>> channelMap = new ConcurrentHashMap<>();

    /**
     * Create OutboundChannelGroup.
     *
     * @param workerGroup            worker group
     * @param eventBus               event-bus
     * @param selfId                 self id
     * @param logReplicationInterval log replication interval
     */
    OutboundChannelGroup(EventLoopGroup workerGroup, EventBus eventBus, NodeId selfId, int logReplicationInterval) {
        this.workerGroup = workerGroup;
        this.eventBus = eventBus;
        this.slefId = selfId;
        this.connectTimeoutMillis = logReplicationInterval / 2;
    }

    /**
     * Get channel to remote node.
     * If channel doesn't exist, it will be created.
     *
     * @param nodeId  id of remote node
     * @param address address of remote node
     * @return nio channel to remote node
     */
    NioChannel getOrConnect(NodeId nodeId, Address address) {
        Future<NioChannel> future = channelMap.get(nodeId);
        if (future == null) {
            FutureTask<NioChannel> newFuture = new FutureTask<>(() -> connect(nodeId, address));
            future = channelMap.putIfAbsent(nodeId, newFuture);
            if (future == null) {
                future = newFuture;
                newFuture.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            channelMap.remove(nodeId);
            if (e instanceof ExecutionException) {
                Throwable cause = e.getCause();
                if (cause instanceof ConnectException) {
                    throw new ChannelConnectException("failed to get channel to node " + nodeId +
                            ", cause " + cause.getMessage(), cause);
                }
            }
            throw new ChannelException("failed to get channel to node " + nodeId, e);
        }
    }

    /**
     * Connect to remote node and return the channel.
     *
     * @param nodeId  id of remote node
     * @param address address of remote node
     * @return nio channel to remote node
     * @throws InterruptedException if interrupted
     */
    private NioChannel connect(NodeId nodeId, Address address) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NodeRpcMessageDecoder());
                        pipeline.addLast(new NodeRpcMessageEncoder());
                        pipeline.addLast(new ToRemoteHandler(eventBus, nodeId, slefId));
                    }
                });
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort()).sync();
        if (!future.isSuccess()) {
            throw new ChannelException("failed to connect", future.cause());
        }
        logger.debug("channel OUTBOUND-{} connected", nodeId);
        Channel nettyChannel = future.channel();
        nettyChannel.closeFuture().addListener((ChannelFutureListener) cf -> {
            logger.debug("channel OUTBOUND-{} disconnected", nodeId);
            channelMap.remove(nodeId);
        });
        return new NioChannel(nettyChannel);
    }

    /**
     * Close all channels.
     */
    void closeAll() {
        logger.debug("close all outbound channels");
        channelMap.forEach((nodeId, nioChannelFuture) -> {
            try {
                nioChannelFuture.get().close();
            } catch (Exception e) {
                logger.warn("failed to close", e);
            }
        });
    }

}
