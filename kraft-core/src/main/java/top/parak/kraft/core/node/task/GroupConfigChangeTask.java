package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeId;

import java.util.concurrent.Callable;

/**
 * Group config change task.
 *
 * @author KHighness
 * @since 2022-05-31
 * @email parakovo@gmail.com
 */
public interface GroupConfigChangeTask extends Callable<GroupConfigChangeTaskResult> {

    public GroupConfigChangeTask NONE;

    boolean isTargetNode(NodeId nodeId);

    void onLogCommitted();

}
