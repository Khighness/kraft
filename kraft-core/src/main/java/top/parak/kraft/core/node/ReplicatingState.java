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
 * is determined by the follower's {@code #matchIndex}. Among the {@code matchIndex}
 * of all followers, more than half of the {@code matchIndex} will become
 * leader's new {@code commitIndex}.
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
     * Create ReplicatingState.
     *
     * @param nextIndex next index
     */
    public ReplicatingState(int nextIndex) {
        this(nextIndex, 0);
    }

    /**
     * Create ReplicatingState.
     *
     * @param nextIndex  next index
     * @param matchIndex match index
     */
    public ReplicatingState(int nextIndex, int matchIndex) {
        this.nextIndex = nextIndex;
        this.matchIndex= matchIndex;
    }

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

    /**
     * Back off next index, in other word, decrease.
     *
     * @return true if decrease successfully
     */
    public boolean backOffNextIndex() {
        if (nextIndex > 1) {
            nextIndex--;
            return true;
        }
        return false;
    }

    /**
     * Advance next index and match index by last entry index.
     *
     * @param lastEntryIndex last entry index
     * @return true if advanced, false if no change
     */
    public boolean advance(int lastEntryIndex) {
        // changed
        boolean result = (matchIndex != lastEntryIndex || nextIndex != (lastEntryIndex + 1));

        matchIndex = lastEntryIndex;
        nextIndex = lastEntryIndex + 1;

        return result;
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
