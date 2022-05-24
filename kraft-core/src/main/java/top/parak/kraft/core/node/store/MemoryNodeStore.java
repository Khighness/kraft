package top.parak.kraft.core.node.store;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Store node status via memory.
 *
 * @author KHighness
 * @since 2022-05-24
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class MemoryNodeStore implements NodeStore {

    private int term;
    private NodeId votedFor;

    public MemoryNodeStore() {
        this(0, null);
    }

    public MemoryNodeStore(int term, NodeId votedFor) {
        this.term = term;
        this.votedFor = votedFor;
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        this.term = term;
    }

    @Nullable
    @Override
    public NodeId getNotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(NodeId nodeId) {
        this.votedFor = votedFor;
    }

    @Override
    public void close() {
    }

}
