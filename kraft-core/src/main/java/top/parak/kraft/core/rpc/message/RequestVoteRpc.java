package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeId;

import java.io.Serializable;

/**
 * RequestVote RPC arguments.
 * <p>Invoker: Candidate</p>
 * <p>Receiver: Follower</p>
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public class RequestVoteRpc implements Serializable {

    /**
     * The term of candidate.
     */
    private int term;
    /**
     * The node id of candidate.
     */
    private NodeId nodeId;
    /**
     * The index of candidate' s last log entry.
     */
    private int lastLogIndex = 0;
    /**
     * The term of candidate' s last log entry.
     */
    private int lastLogTerm = 0;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
    }

    public int getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(int lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    @Override
    public String toString() {
        return "RequestVoteRpc{" +
                "term=" + term +
                ", nodeId=" + nodeId +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }

}
