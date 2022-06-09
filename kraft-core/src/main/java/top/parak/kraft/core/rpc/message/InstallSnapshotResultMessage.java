package top.parak.kraft.core.rpc.message;

import com.google.common.base.Preconditions;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nonnull;

/**
 * InstallSnapshotResult message.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class InstallSnapshotResultMessage {

    private final InstallSnapshotResult result;
    private final NodeId sourceNodeId;
    private final InstallSnapshotRpc rpc;

    public InstallSnapshotResultMessage(InstallSnapshotResult result, NodeId sourceNodeId, @Nonnull InstallSnapshotRpc rpc) {
        Preconditions.checkNotNull(rpc);
        this.result = result;
        this.sourceNodeId = sourceNodeId;
        this.rpc = rpc;
    }

    public InstallSnapshotResult get() {
        return result;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public InstallSnapshotRpc getRpc() {
        return rpc;
    }

}
