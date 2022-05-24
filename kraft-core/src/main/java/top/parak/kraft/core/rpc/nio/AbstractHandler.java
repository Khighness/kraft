package top.parak.kraft.core.rpc.nio;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Channel;
import top.parak.kraft.core.rpc.message.*;

import java.util.Objects;

/**
 * Abstract handler.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public abstract class AbstractHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);

    /**
     * EventBus is a pub-sub component used to publish event to subscribers.
     * This decouples the RAFT algorithm component and RPC implementation component.
     */
    protected final EventBus eventBus;
    /**
     * The if of remote node.
     */
    NodeId remotedId;
    /**
     * RPC channel between remote node and self.
     */
    protected Channel channel;
    /**
     * The last {@link AppendEntriesRpc}.
     */
    private AppendEntriesRpc lastAppendEntriesRpc;
    /**
     * The last {@link InstallSnapshotRpc}.
     */
    private InstallSnapshotRpc lastInstallSnapshotRpc;

    public AbstractHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        assert remotedId != null;
        assert channel != null;

        if (msg instanceof RequestVoteRpc) {
            RequestVoteRpc rpc = (RequestVoteRpc) msg;
            eventBus.post(new RequestVoteRpcMessage(rpc, remotedId, channel));
        } else if (msg instanceof RequestVoteResult) {
            eventBus.post(msg);
        } else if (msg instanceof AppendEntriesRpc) {
            AppendEntriesRpc rpc = (AppendEntriesRpc) msg;
        } else if (msg instanceof AppendEntriesResult) {
            AppendEntriesResult result = (AppendEntriesResult) msg;
            if (lastAppendEntriesRpc == null) {
                logger.warn("no last append entries rpc");
            } else {
                if (!Objects.equals(result.getRpcMessageId(), lastAppendEntriesRpc.getMessageId())) {
                    logger.warn("incorrect append entries rpc message id {}, excepted {}",
                            result.getRpcMessageId(), lastAppendEntriesRpc.getMessageId());
                } else {
                    eventBus.post(new AppendEntriesResultMessage(result, remotedId, lastAppendEntriesRpc));
                    lastAppendEntriesRpc = null;
                }
            }
        } else if (msg instanceof InstallSnapshotRpc) {
            InstallSnapshotRpc rpc = (InstallSnapshotRpc) msg;
            eventBus.post(new InstallSnapshotRpcMessage(rpc, remotedId, channel));
        } else if (msg instanceof InstallSnapshotResult) {
            InstallSnapshotResult result = (InstallSnapshotResult) msg;
            assert lastAppendEntriesRpc != null;
            eventBus.post(new InstallSnapshotResultMessage(result, remotedId, lastInstallSnapshotRpc));
            lastAppendEntriesRpc = null;
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AppendEntriesRpc) {
            lastAppendEntriesRpc = (AppendEntriesRpc) msg;
        } else if (msg instanceof InstallSnapshotRpc) {
            lastInstallSnapshotRpc = (InstallSnapshotRpc) msg;
        }
        super.write(ctx, msg, promise);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage(), cause);
        ctx.close();
    }

}
