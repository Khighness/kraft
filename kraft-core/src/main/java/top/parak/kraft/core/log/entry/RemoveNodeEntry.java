package top.parak.kraft.core.log.entry;

import top.parak.kraft.core.Protos;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group config log entry for removing node.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class RemoveNodeEntry extends GroupConfigEntry {

    /**
     * The id of the node to be removed.
     */
    private final NodeId nodeToRemove;

    /**
     * Create AddNodeEntry.
     *
     * @param index         the index of the membership-change log entry
     * @param term          the term of the membership-change log entry
     * @param nodeEndpoints the endpoints of the nodes in group
     * @param nodeToRemove  the id of the node to be removed
     */
    public RemoveNodeEntry(int index, int term, Set<NodeEndpoint> nodeEndpoints, NodeId nodeToRemove) {
        super(KIND_REMOVE_NODE, index, term, nodeEndpoints);
        this.nodeToRemove = nodeToRemove;
    }

    public Set<NodeEndpoint> getResultNodeEndpoints() {
        return getNodeEndpoints().stream()
                .filter(c -> !c.getId().equals(nodeToRemove))
                .collect(Collectors.toSet());
    }

    public NodeId getNodeToRemove() {
        return nodeToRemove;
    }

    @Override
    public byte[] getCommandBytes() {
        return Protos.RemoveNodeCommand.newBuilder()
                .addAllNodeEndpoints(getNodeEndpoints().stream().map(c ->
                        Protos.NodeEndpoint.newBuilder()
                                .setId(c.getId().getValue())
                                .setHost(c.getHost())
                                .setPort(c.getPort())
                                .build()
                ).collect(Collectors.toList()))
                .setNodeToRemove(nodeToRemove.getValue())
                .build().toByteArray();
    }

    @Override
    public String toString() {
        return "RemoveNodeEntry{" +
                "index=" + index +
                ", term=" + term +
                ", nodeEndpoints=" + getNodeEndpoints() +
                ", nodeToRemove=" + nodeToRemove +
                '}';
    }

}
