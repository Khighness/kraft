package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * Task context for {@link NewNodeCatchUpTask}.
 */
public interface NewNodeCatchUpTaskContext {

    /**
     * Replicate log to new node.
     * <p>
     * Process will be run in node task executor.
     * </p>
     *
     * @param endpoint endpoint
     */
    void replicateLog(NodeEndpoint endpoint);

    /**
     * Replicate log to endpoint.
     *
     * @param endpoint  endpoint
     * @param nextIndex next index
     */
    void doReplicateLog(NodeEndpoint endpoint, int nextIndex);

    void sendInstallSnapshot(NodeEndpoint endpoint, int offset);

    /**
     * Done and remove current task.
     *
     * @param task task
     */
    void done(NewNodeCatchUpTask task);

}
