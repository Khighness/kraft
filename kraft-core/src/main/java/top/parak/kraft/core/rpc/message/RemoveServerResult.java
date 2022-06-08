package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * RemoveServer RPC result.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
public class RemoveServerResult {

    private final GroupConfigChangeStatus status;
    private final NodeEndpoint leaderHint;

    public RemoveServerResult(GroupConfigChangeStatus status, NodeEndpoint leaderHint) {
        this.status = status;
        this.leaderHint = leaderHint;
    }

    public GroupConfigChangeStatus getStatus() {
        return status;
    }

    public NodeEndpoint getLeaderHint() {
        return leaderHint;
    }

    @Override
    public String toString() {
        return "RemoveServerResult{" +
                "status=" + status +
                ", leaderHint=" + leaderHint +
                '}';
    }

}
