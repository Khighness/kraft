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
class ReplicatingState {

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
    ReplicatingState(int nextIndex) {
        this(nextIndex, 0);
    }

    /**
     * Create ReplicatingState.
     *
     * @param nextIndex  next index
     * @param matchIndex match index
     */
    ReplicatingState(int nextIndex, int matchIndex) {
        this.nextIndex = nextIndex;
        this.matchIndex = matchIndex;
    }

    /**
     * Get the index of the next log entry that needs to be sent to the follower.
     *
     * @return next index
     */
    int getNextIndex() {
        return nextIndex;
    }

    /**
     * Get the index of the last log entry that has been replicated to the follower.
     *
     * @return match index
     */
    int getMatchIndex() {
        return matchIndex;
    }

    /**
     * Back off next index, in other word, decrease.
     *
     * @return true if decrease successfully, false if next index is less than or equal to {@code 1}
     */
    boolean backOffNextIndex() {
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
    boolean advance(int lastEntryIndex) {
        // changed
        boolean result = (matchIndex != lastEntryIndex || nextIndex != (lastEntryIndex + 1));

        matchIndex = lastEntryIndex;
        nextIndex = lastEntryIndex + 1;

        return result;
    }

    /**
     * Test if replicating.
     *
     * @return true if replicating, otherwise false
     */
    boolean isReplicating() {
        return replicating;
    }

    /**
     * Set replicating.
     *
     * @param replicating replicating
     */
    void setReplicating(boolean replicating) {
        this.replicating = replicating;
    }

    /**
     * Get last replicated timestamp.
     *
     * @return last replicated timestamp
     */
    long getLastReplicatedAt() {
        return lastReplicatedAt;
    }

    /**
     * Set last replicated timestamp.
     *
     * @param lastReplicatedAt last replicated timestamp
     */
    void setLastReplicatedAt(long lastReplicatedAt) {
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
