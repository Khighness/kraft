package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.message.AppendEntriesResultMessage;
import top.parak.kraft.core.rpc.message.InstallSnapshotResultMessage;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Group for {@link NewNodeCatchUpTask}.
 *
 * @author KHighness
 * @since 2022-06-03
 * @email parakovo@gmail.com
 */
@ThreadSafe
public class NewNodeCatchUpTaskGroup {

    private final ConcurrentMap<NodeId, NewNodeCatchUpTask> taskMap = new ConcurrentHashMap<>();

    /**
     * Add task.
     *
     * @param task task
     * @return true if successfully, false if task for same node exists
     */
    public boolean add(NewNodeCatchUpTask task) {
        return taskMap.putIfAbsent(task.getNodeId(), task) == null;
    }

    /**
     * Remove task.
     *
     * @param task task
     * @return true if removed, false if not found
     */
    public boolean remove(NewNodeCatchUpTask task) {
        return taskMap.remove(task.getNodeId()) != null;
    }

    /**
     * Invoke <code>onReceiveAppendEntriesResult</code> on task.
     *
     * @param resultMessage result message
     * @param nextLogIndex  index of next log
     * @return true if invoked, false if no task for node
     */
    public boolean onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage, int nextLogIndex) {
        NewNodeCatchUpTask task = taskMap.get(resultMessage.getSourceNodeId());
        if (task == null) {
            return false;
        }
        task.onReceiveAppendEntriesResult(resultMessage, nextLogIndex);
        return true;
    }

    /**
     * Invoke <code>onReceiveInstallSnapshotResult</code> on task.
     *
     * @param resultMessage result message
     * @param nextLogIndex  index of next log
     * @return true if invoked, false if no task for node
     */
    public boolean onReceiveInstallSnapshotResult(InstallSnapshotResultMessage resultMessage, int nextLogIndex) {
        NewNodeCatchUpTask task = taskMap.get(resultMessage.getSourceNodeId());
        if (task == null) {
            return false;
        }
        task.onReceiveInstallSnapshotResult(resultMessage, nextLogIndex);
        return true;
    }

}
