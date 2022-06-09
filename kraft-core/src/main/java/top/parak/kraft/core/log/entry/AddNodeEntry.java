package top.parak.kraft.core.log.entry;

import top.parak.kraft.core.Protos;
import top.parak.kraft.core.node.NodeEndpoint;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group config log entry for adding node.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class AddNodeEntry extends GroupConfigEntry {

    /**
     * The endpoint of the node to be added.
     */
    private final NodeEndpoint newNodeEndpoint;

    /**
     * Create AddNodeEntry.
     *
     * @param index           the index of the membership-change log entry
     * @param term            the term of the membership-change log entry
     * @param nodeEndpoints   the endpoints of the nodes in group
     * @param newNodeEndpoint the endpoint of the node to be added
     */
    public AddNodeEntry(int index, int term, Set<NodeEndpoint> nodeEndpoints, NodeEndpoint newNodeEndpoint) {
        super(KIND_ADD_NODE, index, term, nodeEndpoints);
        this.newNodeEndpoint = newNodeEndpoint;
    }

    public NodeEndpoint getNewNodeEndpoint() {
        return newNodeEndpoint;
    }

    public Set<NodeEndpoint> getResultNodeEndpoints() {
        Set<NodeEndpoint> configs = new HashSet<>(getNodeEndpoints());
        configs.add(newNodeEndpoint);
        return configs;
    }

    @Override
    public byte[] getCommandBytes() {
        return Protos.AddNodeCommand.newBuilder()
                .addAllNodeEndpoints(getNodeEndpoints().stream().map(c ->
                        Protos.NodeEndpoint.newBuilder()
                                .setId(c.getId().getValue())
                                .setHost(c.getHost())
                                .setPort(c.getPort())
                                .build()
                ).collect(Collectors.toList()))
                .setNewNodeEndpoint(Protos.NodeEndpoint.newBuilder()
                        .setId(newNodeEndpoint.getId().getValue())
                        .setHost(newNodeEndpoint.getHost())
                        .setPort(newNodeEndpoint.getPort())
                        .build()
                ).build().toByteArray();
    }

    @Override
    public String toString() {
        return "AddNodeEntry{" +
                "index=" + index +
                ", term=" + term +
                ", nodeEndpoints=" + getNodeEndpoints() +
                ", newNodeEndpoint=" + newNodeEndpoint +
                '}';
    }

}
