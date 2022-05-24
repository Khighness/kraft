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
     * Follower id, receiver id.
     */
    private final NodeId sourceId;
    /**
     * InstallSnapshot RPC arguments.
     */
    private final InstallSnapshotRpc rpc;

    public InstallSnapshotResultMessage(InstallSnapshotResult result, NodeId sourceId, InstallSnapshotRpc rpc) {
        this.result = result;
        this.sourceId = sourceId;
        this.rpc = rpc;
    }

    public InstallSnapshotResult getResult() {
        return result;
    }

    public NodeId getSourceId() {
        return sourceId;
    }

    public InstallSnapshotRpc getRpc() {
        return rpc;
    }

    @Override
    public String toString() {
        return "InstallSnapshotResultMessage{" +
                "result=" + result +
                ", sourceId=" + sourceId +
                ", rpc=" + rpc +
                '}';
    }

}
