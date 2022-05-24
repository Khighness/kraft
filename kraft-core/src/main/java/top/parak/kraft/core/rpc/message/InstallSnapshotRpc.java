package top.parak.kraft.core.rpc.message;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

import java.util.Set;

/**
 * InstallSnapshot RPC arguments.
 * <p>Invoker: Leader</p>
 * <p>Receiver: Follower</p>
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public class InstallSnapshotRpc {

    /**
     * The leader's term.
     */
    private int term;
    /**
     * The id of leader, so follower can redirect clients.
     */
    private NodeId leaderId;
    /**
     * The snapshot replaces all entries up through and including this index.
     */
    private int lastIndex;
    /**
     * The term of lastIndex.
     */
    private int lastTerm;
    /**
     * Current node configs.
     */
    private Set<NodeEndpoint> lastConfig;
    /**
     * The byte offset where chunk id position in the snapshot file.
     */
    private int offset;
    /**
     * The raw bytes of the snapshot chunk, starting at offset.
     */
    private byte[] data;
    /**
     * True if this is the last chunk.
     */
    private boolean done;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public NodeId getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(NodeId leaderId) {
        this.leaderId = leaderId;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getLastTerm() {
        return lastTerm;
    }

    public void setLastTerm(int lastTerm) {
        this.lastTerm = lastTerm;
    }

    public Set<NodeEndpoint> getLastConfig() {
        return lastConfig;
    }

    public void setLastConfig(Set<NodeEndpoint> lastConfig) {
        this.lastConfig = lastConfig;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getData() {
        return data;
    }

    public int getDataLength() {
        return data.length;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "InstallSnapshotRpc{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", lastIndex=" + lastIndex +
                ", lastTerm=" + lastTerm +
                ", lastConfig=" + lastConfig +
                ", offset=" + offset +
                ", data=" + new String(data) +
                ", done=" + done +
                '}';
    }

}
