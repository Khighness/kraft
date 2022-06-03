package top.parak.kraft.core.node.config;

import top.parak.kraft.core.log.Log;

/**
 * Node configuration.
 * <p>
 * Node configuration should not change after initialization.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
public class NodeConfig {

    /**
     * Minimum election timeout.
     */
    private int minElectionTimeout = 3000;

    /**
     * Maximum election timeout.
     */
    private int maxElectionTimeout = 6000;

    /**
     * Delay for first log replication after becoming leader.
     */
    private int logReplicationDelay = 0;

    /**
     * Interval for log replication task.
     * <p>
     * More specifically, interval for heartbeat rpc.
     * Append entries rpc maybe less than this interval.
     * </p>
     */
    private int logReplicationInterval = 1000;

    /**
     * Read timeout to receive response from follower.
     * <p>
     * If no response received from follower, resend log replication rpc.
     * </p>
     */
    private int logReplicationReadTimeout = 900;

    /**
     * Max entries to send when replicating log to followers
     */
    private int maxReplicationEntries = Log.ALL_ENTRIES;

    /**
     * Max entries to send when replicating log to new node.
     */
    private int maxReplicationEntriesForNewNode = Log.ALL_ENTRIES;

    /**
     * Data length in install snapshot rpc.
     */
    private int snapshotDataLength = 1024;

    /**
     * Worker thread count in nio connector.
     */
    private int nioWorkerThreads = 0;

    /**
     * Max round for new node to catch up.
     */
    private int newNodeMaxRound = 10;

    /**
     * Read timeout to receive response form new node.
     * <p>
     * Default is election timeout.
     * </p>
     */
    private int newNodeReadTimeout = 3000;

    /**
     * Timeout for new node to make progress.
     * <p>
     * If new node cannot make progress after this timeout, it cannot be added to group and reply TIMEOUT.
     * </p>
     * <p>
     * Default to election timeout.
     * </p>
     */
    private int newNodeAdvanceTimeout = 3000;

    /**
     * Timeout to wait for previous group config change to complete.
     * <p>
     * Default is {@code 0}, forever.
     * </p>
     */
    private int previousGroupConfigChangeTimeout = 0;

    public int getMinElectionTimeout() {
        return minElectionTimeout;
    }

    public void setMinElectionTimeout(int minElectionTimeout) {
        this.minElectionTimeout = minElectionTimeout;
    }

    public int getMaxElectionTimeout() {
        return maxElectionTimeout;
    }

    public void setMaxElectionTimeout(int maxElectionTimeout) {
        this.maxElectionTimeout = maxElectionTimeout;
    }

    public int getLogReplicationDelay() {
        return logReplicationDelay;
    }

    public void setLogReplicationDelay(int logReplicationDelay) {
        this.logReplicationDelay = logReplicationDelay;
    }

    public int getLogReplicationInterval() {
        return logReplicationInterval;
    }

    public void setLogReplicationInterval(int logReplicationInterval) {
        this.logReplicationInterval = logReplicationInterval;
    }

    public int getLogReplicationReadTimeout() {
        return logReplicationReadTimeout;
    }

    public void setLogReplicationReadTimeout(int logReplicationReadTimeout) {
        this.logReplicationReadTimeout = logReplicationReadTimeout;
    }

    public int getMaxReplicationEntries() {
        return maxReplicationEntries;
    }

    public void setMaxReplicationEntries(int maxReplicationEntries) {
        this.maxReplicationEntries = maxReplicationEntries;
    }

    public int getMaxReplicationEntriesForNewNode() {
        return maxReplicationEntriesForNewNode;
    }

    public void setMaxReplicationEntriesForNewNode(int maxReplicationEntriesForNewNode) {
        this.maxReplicationEntriesForNewNode = maxReplicationEntriesForNewNode;
    }

    public int getSnapshotDataLength() {
        return snapshotDataLength;
    }

    public void setSnapshotDataLength(int snapshotDataLength) {
        this.snapshotDataLength = snapshotDataLength;
    }

    public int getNioWorkerThreads() {
        return nioWorkerThreads;
    }

    public void setNioWorkerThreads(int nioWorkerThreads) {
        this.nioWorkerThreads = nioWorkerThreads;
    }

    public int getNewNodeMaxRound() {
        return newNodeMaxRound;
    }

    public void setNewNodeMaxRound(int newNodeMaxRound) {
        this.newNodeMaxRound = newNodeMaxRound;
    }

    public int getNewNodeReadTimeout() {
        return newNodeReadTimeout;
    }

    public void setNewNodeReadTimeout(int newNodeReadTimeout) {
        this.newNodeReadTimeout = newNodeReadTimeout;
    }

    public int getNewNodeAdvanceTimeout() {
        return newNodeAdvanceTimeout;
    }

    public void setNewNodeAdvanceTimeout(int newNodeAdvanceTimeout) {
        this.newNodeAdvanceTimeout = newNodeAdvanceTimeout;
    }

    public int getPreviousGroupConfigChangeTimeout() {
        return previousGroupConfigChangeTimeout;
    }

    public void setPreviousGroupConfigChangeTimeout(int previousGroupConfigChangeTimeout) {
        this.previousGroupConfigChangeTimeout = previousGroupConfigChangeTimeout;
    }

}
