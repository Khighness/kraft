package top.parak.kraft.core.rpc.nio;

import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.node.role.LeaderNodeRole;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The container used to manage inbound connections (the channels from remote node).
 * <p>
 * This container doesn't need to search channel by {@link NodeId}, it just record all connections
 * from remote nodes.
 * </p>
 * <p>
 * In addition, it needs to reset all channels when the current node become {@link LeaderNodeRole},
 * which is equivalent to closing all connections.
 * </p>
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
@ThreadSafe
public class InboundChannelGroup {

    private static final Logger logger = LoggerFactory.getLogger(InboundChannelGroup.class);

    /**
     * The list to record all channels from remote nodes.
     */
    private final List<NioChannel> channels = new CopyOnWriteArrayList<>();

    /**
     * Add inbound channel.
     *
     * @param remotedId id of remote node
     * @param channel   channel between remote node and self
     */
    public void add(NodeId remotedId, NioChannel channel) {
        logger.debug("channel INBOUND-{} connected", remotedId);
        channel.getDelegate().closeFuture().addListener((ChannelFutureListener) future -> {
           logger.debug("channel INBOUND-{} disconnected", remotedId);
        });
    }

    /**
     * Remove inbound channel.
     *
     * @param channel channel between remote node and self
     */
    public void remove(NioChannel channel) {
        channels.remove(channel);
    }

    /**
     * Close all inbound channels.
     */
    void closeAll() {
        logger.debug("close all inbound connections");
        for (NioChannel channel : channels) {
            channel.close();
        }
    }

}
