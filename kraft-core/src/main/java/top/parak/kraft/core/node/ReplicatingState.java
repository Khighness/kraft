package top.parak.kraft.core.node;

/**
 * Replicating State.
 * <p>
 * In order to track the replication progress of each follower, the leader
 * needs to record the index of the next log entry to be replicated which
 * is defined as {@link ReplicatingState#nextIndex} and the index of the
 * matched logs which is defined as {@link ReplicatingState#matchIndex}.
 * </p>
 * <p>
 * In the entire log replication process, the leader's {@code commitIndex}
 * is determined by the follower's {@link ReplicatingState}. Among the
 * {@link ReplicatingState#matchIndex} of all followers, more than half
 * of the {@link ReplicatingState#matchIndex} will become leader's new
 * {@code commitIndex}.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
public class ReplicatingState {

    /**
     * The index of the next log entry that needs to be sent to the follower.
     */
    private int nextIndex;

    /**
     * The index of the last log entry that has been replicated to the follower.
     */
    private int matchIndex;

    /**
     * The replicating status.
     */
    private boolean replicating = false;

    /**
     * The last replicated timestamp.
     */
    private long lastReplicatedAt = 0;

    /**
     * Get the index of the next log entry that needs to be sent to the follower.
     *
     * @return next index
     */
    public int getNextIndex() {
        return nextIndex;
    }

    /**
     * Set the index of the next log entry that needs to be sent to the follower.
     *
     * @param nextIndex the index of the next log that needs to be sent to the follower
     */
    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    /**
     * Get the index of the last log entry that has been replicated to the follower.
     *
     * @return match index
     */
    public int getMatchIndex() {
        return matchIndex;
    }

    /**
     * Set the index of the last log entry that has been replicated to the follower.
     *
     * @param matchIndex match index
     */
    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    /**
     * Test if replicating.
     *
     * @return true if replicating, otherwise false
     */
    public boolean isReplicating() {
        return replicating;
    }

    /**
     * Set replicating.
     *
     * @param replicating replicating
     */
    public void setReplicating(boolean replicating) {
        this.replicating = replicating;
    }

    /**
     * Get the last replicated timestamp.
     *
     * @return last replicated timestamp
     */
    public long getLastReplicatedAt() {
        return lastReplicatedAt;
    }

    /**
     * Set the last replicated timestamp.
     *
     * @param lastReplicatedAt last replicated timestamp
     */
    public void setLastReplicatedAt(long lastReplicatedAt) {
        this.lastReplicatedAt = lastReplicatedAt;
    }

    @Override
    public String toString() {
        return "ReplicatingState{" +
                "nextIndex=" + nextIndex +
                ", matchIndex=" + matchIndex +
                ", replicating=" + replicating +
                ", lastReplicatedAt=" + lastReplicatedAt +
                '}';
    }

}
