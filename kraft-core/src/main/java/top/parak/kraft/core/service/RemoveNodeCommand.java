package top.parak.kraft.core.service;

import top.parak.kraft.core.node.NodeId;

/**
 * @author KHighness
 * @since 2022-03-30
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
