package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

/**
 * AddNode task.
 *
 * @author KHighness
 * @since 2022-05-31
 * @email parakovo@gmail.com
 */
public class AddNodeTask extends AbstractGroupConfigChangeTask {

    private final NodeEndpoint endpoint;
    private final int nextIndex;
    private final int matchIndex;

    public AddNodeTask(GroupConfigChangeTaskContext context, NodeEndpoint endpoint, NewNodeCatchUpTaskResult newNodeCatchUpTaskResult) {
        this(context, endpoint, newNodeCatchUpTaskResult.getNextIndex(), newNodeCatchUpTaskResult.getMatchIndex());
    }

    public AddNodeTask(GroupConfigChangeTaskContext context, NodeEndpoint endpoint, int nextIndex, int matchIndex) {
        super(context);
        this.endpoint = endpoint;
        this.nextIndex = nextIndex;
        this.matchIndex = matchIndex;
    }

    @Override
    protected void appendGroupConfig() {
        context.addNode(endpoint, nextIndex, matchIndex);
    }

    @Override
    public boolean isTargetNode(NodeId nodeId) {
        return endpoint.getId().equals(nodeId);
    }

    @Override
    public String toString() {
        return "AddNodeTask{" +
                "endpoint=" + endpoint +
                ", nextIndex=" + nextIndex +
                ", matchIndex=" + matchIndex +
                ", state=" + state +
                '}';
    }

}
