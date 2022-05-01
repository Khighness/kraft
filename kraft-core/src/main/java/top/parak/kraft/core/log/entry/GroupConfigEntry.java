package top.parak.kraft.core.log.entry;

import top.parak.kraft.core.node.NodeEndpoint;

import java.util.Set;

/**
 * Group config updating entry.
 * @see AddNodeEntry
 * @see RemoveNodeEntry
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public abstract class GroupConfigEntry extends AbstractEntry {

    /**
     * The endpoints of the nodes in group
     */
    private final Set<NodeEndpoint> nodeEndpoints;

    protected GroupConfigEntry(int kind, int index, int term, Set<NodeEndpoint> nodeEndpoints) {
        super(kind, index, term);
        this.nodeEndpoints = nodeEndpoints;
    }

    public Set<NodeEndpoint> getNodeEndpoints() {
        return nodeEndpoints;
    }

    public abstract Set<NodeEndpoint> getResultNodeEndpoints();

}

