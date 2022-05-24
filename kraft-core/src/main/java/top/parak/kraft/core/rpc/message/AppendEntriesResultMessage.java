package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;

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
    private final NodeId nodeId;
    /**
     * AppendEntries RPC arguments.
     */
    private final AppendEntriesRpc rpc;

    public AppendEntriesResultMessage(AppendEntriesResult result, NodeId nodeId, AppendEntriesRpc rpc) {
        this.result = result;
        this.nodeId = nodeId;
        this.rpc = rpc;
    }

    public AppendEntriesResult getResult() {
        return result;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public AppendEntriesRpc getRpc() {
        return rpc;
    }

    @Override
    public String toString() {
        return "AppendEntriesResultMessage{" +
                "result=" + result +
                ", nodeId=" + nodeId +
                ", rpc=" + rpc +
                '}';
    }

}
