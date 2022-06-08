package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeId;

import java.util.concurrent.Callable;

public interface GroupConfigChangeTask extends Callable<GroupConfigChangeTaskResult> {

    GroupConfigChangeTask NONE = new NullGroupConfigChangeTask();

    boolean isTargetNode(NodeId nodeId);

    void onLogCommitted();

}
