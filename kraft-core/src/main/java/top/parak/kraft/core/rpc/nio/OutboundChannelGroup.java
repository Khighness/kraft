package top.parak.kraft.core.rpc.nio;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

/**
 * The container to manage outbound channels (channels to remote channels).
 *
 * @author KHighness
 * @since 2022-05-25
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class OutboundChannelGroup {

    private static final Logger logger = LoggerFactory.getLogger(OutboundChannelGroup.class);
    private final EventLoopGroup workerGroup;
    private final EventBus eventBus;
    private final NodeId selfId;
    private final int connectTimeoutMills;
    private final ConcurrentHashMap<NodeId, Future<NioChannel>> channelMap = new ConcurrentHashMap<>();

    public OutboundChannelGroup(EventLoopGroup workerGroup, EventBus eventBus, NodeId selfId, int connectTimeoutMills) {
        this.workerGroup = workerGroup;
        this.eventBus = eventBus;
        this.selfId = selfId;
        this.connectTimeoutMills = connectTimeoutMills;
    }

    /**
     * Get channel to remote node.
     * If channel doesn't exist, it will be created.
     *
     * @param nodeId  id of remote node
     * @param address address of remote node
     * @return nio channel to remote node
     */
    public NioChannel getOrConnect(NodeId nodeId, Address address) {
        Future<NioChannel> future = channelMap.get(nodeId);
        if (future == null) {
            FutureTask futureTask = new FutureTask<>(() -> connect(nodeId, address));
        }

        return null;
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
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMills)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new NodeRpcMessageDecoder());
                        p.addLast(new NodeRpcMessageEncoder());
                        p.addLast(new ToRemoteHandler(eventBus, nodeId, selfId));
                    }
                });
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort()).sync();
        if (!future.isSuccess()) {
            throw new ChannelException("failed to connect " + address, future.cause());
        }
        logger.debug("channel OUTBOUND-{} connected", nodeId);
        Channel nettyChannel = future.channel();
        nettyChannel.closeFuture().addListener((ChannelFutureListener) cf -> {
            logger.debug("channel OUTBOUND-{} disconnected", nodeId);
            channelMap.remove(nodeId);
        });
        return new NioChannel(nettyChannel);
    }

    void closeAll() {
        logger.debug("close all outbound channels");
        channelMap.forEach((nodeId, nioChannelFuture) -> {
            try {
                nioChannelFuture.get().close();
            } catch (Exception e) {
                logger.debug("failed to close outbound channels", e);
            }
        });
    }

}
