package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeId;

/**
 * Null group config change task.
 *
 * @author KHighness
 * @since 2022-05-31
 * @email parakovo@gmail.com
 */
public class NullGroupConfigChangeTask implements GroupConfigChangeTask {

    @Override
    public boolean isTargetNode(NodeId nodeId) {
        return false;
    }

    @Override
    public void onLogCommitted() {
    }

    @Override
    public GroupConfigChangeTaskResult call() throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "NullGroupConfigChangeTask{}";
    }

}
