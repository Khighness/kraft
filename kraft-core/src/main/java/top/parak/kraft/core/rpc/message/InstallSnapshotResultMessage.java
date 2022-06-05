package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;

/**
 * InstallSnapshotResult message.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class InstallSnapshotResultMessage {

    /**
     * InstallSnapshot RPC result.
     */
    private final InstallSnapshotResult result;
    /**
     * Leader id, invoker id.
     */
    private final NodeId sourceNodeId;
    /**
     * InstallSnapshot RPC arguments.
     */
    private final InstallSnapshotRpc rpc;

    public InstallSnapshotResultMessage(InstallSnapshotResult result, NodeId sourceNodeId, InstallSnapshotRpc rpc) {
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

    @Override
    public String toString() {
        return "InstallSnapshotResultMessage{" +
                "result=" + result +
                ", sourceNodeId=" + sourceNodeId +
                ", rpc=" + rpc +
                '}';
    }

}
