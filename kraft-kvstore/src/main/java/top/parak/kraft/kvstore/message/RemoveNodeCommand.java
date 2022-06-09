package top.parak.kraft.kvstore.message;

import top.parak.kraft.core.node.NodeId;

/**
 * RemoveNodeCommand message.
 *
 * @author KHighness
 * @since 2022-06-09
 * @email parakovo@gmail.com
 */
public class RemoveNodeCommand {

    private final NodeId nodeId;

    public RemoveNodeCommand(String nodeId) {
        this.nodeId = new NodeId(nodeId);
    }

    public NodeId getNodeId() {
        return nodeId;
    }

}
