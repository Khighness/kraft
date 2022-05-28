package top.parak.kraft.core.node;

/**
 * State of group member.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 * @see ReplicatingState
 */
public class GroupMember {

    private final NodeEndpoint endpoint;
    private ReplicatingState replicatingState;
    private boolean major;
    private boolean removing = false;

    GroupMember(NodeEndpoint endpoint) {
        this(endpoint, null, true);
    }

    GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState, boolean major) {
        this.endpoint = endpoint;
        this.replicatingState = replicatingState;
        this.major = major;
    }

    public NodeEndpoint getEndpoint() {
        return endpoint;
    }

    public ReplicatingState getReplicatingState() {
        return replicatingState;
    }

    public void setReplicatingState(ReplicatingState replicatingState) {
        this.replicatingState = replicatingState;
    }

    public boolean isMajor() {
        return major;
    }

    public void setMajor(boolean major) {
        this.major = major;
    }

    public boolean isRemoving() {
        return removing;
    }

    public void setRemoving() {
        this.removing = true;
    }

    private ReplicatingState ensureReplicatingState() {
        if (replicatingState == null) {
            throw new IllegalStateException("replication state not set");
        }
        return replicatingState;
    }

    public NodeId getId() {
        return endpoint.getId();
    }

    public boolean idEquals(NodeId id) {
        return endpoint.getId().equals(id);
    }

    public int getNextIndex() {
        return ensureReplicatingState().getNextIndex();
    }

    public int getMatchIndex() {
        return ensureReplicatingState().getMatchIndex();
    }

    public boolean advanceReplicatingState(int lastEntryIndex) {
        return ensureReplicatingState().advance(lastEntryIndex);
    }

    public boolean backOffNextIndex() {
        return ensureReplicatingState().backOffNextIndex();
    }

    public void replicatedNow() {
        replicateAt(System.currentTimeMillis());
    }

    public void replicateAt(long replicatedAt) {
        ReplicatingState replicatingState = ensureReplicatingState();
        replicatingState.setReplicating(true);
        replicatingState.setLastReplicatedAt(replicatedAt);
    }

    public boolean isReplicating() {
        return ensureReplicatingState().isReplicating();
    }

    public void stopReplicating() {
        ensureReplicatingState().setReplicating(false);
    }

    /**
     * Check if follower should replicate.
     * <p>
     * Return true if
     * <ol>
     * <li>not replicating</li>
     * <li>replicated but no response in specified timeout</li>
     * </ol>
     * </p>
     *
     * @return true if should, otherwise false
     */
    public boolean shouldReplicate(long readTimeout) {
        ReplicatingState replicatingState = ensureReplicatingState();
        return !replicatingState.isReplicating() ||
                System.currentTimeMillis() - replicatingState.getLastReplicatedAt() >= readTimeout;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "endpoint=" + endpoint +
                ", replicatingState=" + replicatingState +
                ", major=" + major +
                ", removing=" + removing +
                '}';
    }

}
