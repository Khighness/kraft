package top.parak.kraft.core.node.store;

import top.parak.kraft.core.node.NodeId;

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

    /**
     * The currentTerm of node.
     */
    private int term;
    /**
     * The node id voted for of node.
     */
    private NodeId votedFor;

    /**
     * Create MemoryNodeStore。
     */
    public MemoryNodeStore() {
        this(0, null);
    }

    /**
     * Create MemoryNodeStore.
     *
     * @param term     the currentTerm of node
     * @param votedFor the node id voted for of node
     */
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

    @Override
    public NodeId getVotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(NodeId votedFor) {
        this.votedFor = votedFor;
    }

    @Override
    public void close() {
    }

}
