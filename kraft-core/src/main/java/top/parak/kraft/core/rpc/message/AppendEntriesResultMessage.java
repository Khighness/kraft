package top.parak.kraft.core.rpc.message;

import com.google.common.base.Preconditions;
import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nonnull;

/**
 * AppendEntriesResult message.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class AppendEntriesResultMessage {

    /**
     * AppendEntries RPC result.
     */
    private final AppendEntriesResult result;
    /**
     * Leader id, invoker id.
     */
    private final NodeId sourceNodeId;
    /**
     * AppendEntries RPC arguments.
     */
    private final AppendEntriesRpc rpc;

    public AppendEntriesResultMessage(AppendEntriesResult result, NodeId sourceNodeId, @Nonnull AppendEntriesRpc rpc) {
        Preconditions.checkNotNull(rpc);
        this.result = result;
        this.sourceNodeId = sourceNodeId;
        this.rpc = rpc;
    }

    public AppendEntriesResult get() {
        return result;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public AppendEntriesRpc getRpc() {
        return rpc;
    }

}
