package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

/**
 * Task context for {@link GroupConfigChangeTask}.
 *
 * @author KHighness
 * @since 2022-06-01
 * @email parakovo@gmail.com
 */
public interface GroupConfigChangeTaskContext {

    /**
     * Add node.
     * <p>
     * Process will be run in node task executor.
     * </p>
     * <ol>
     * <li>add node to group</li>
     * <li>append log entry</li>
     * <li>replicate</li>
     * </ol>
     *
     * @param endpoint   endpoint
     * @param nextIndex  next index
     * @param matchIndex match index
     */
    void addNode(NodeEndpoint endpoint, int nextIndex, int matchIndex);

    /**
     * Downgrade node.
     * <p>
     * Process will be run in node task executor.
     * </p>
     * <ol>
     * <li>downgrade node</li>
     * <li>append log entry</li>
     * <li>replicate</li>
     * </ol>
     *
     * @param nodeId node id
     */
    void downgradeNode(NodeId nodeId);

    /**
     * Remove node.
     * <p>
     * Process will be run in node task executor
     * </p>
     * <p>
     * If node id is self id, step down.
     * </p>
     *
     * @param nodeId node id
     */
    void removeNode(NodeId nodeId);

    /**
     * Done and remove current group config change task.
     */
    void done();

}
