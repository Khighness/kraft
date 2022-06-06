package top.parak.kraft.core.node.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.node.NodeId;

/**
 * RemoveNode task.
 *
 * @author KHighness
 * @since 2022-06-02
 * @email parakovo@gmail.com
 */
public class RemoveNodeTask extends AbstractGroupConfigChangeTask {

    private static final Logger logger = LoggerFactory.getLogger(RemoveNodeTask.class);
    private final NodeId nodeId;

    public RemoveNodeTask(GroupConfigChangeTaskContext context, NodeId nodeId) {
        super(context);
        this.nodeId = nodeId;
    }

    @Override
    protected void appendGroupConfig() {
        context.downgradeNode(nodeId);
    }

    @Override
    public boolean isTargetNode(NodeId nodeId) {
        return this.nodeId.equals(nodeId);
    }

    @Override
    public synchronized void onLogCommitted() {
        if (state != State.GROUP_CONFIG_APPENDED) {
            throw new IllegalStateException("log committed before log appended");
        }
        setState(State.GROUP_CONFIG_COMMITTED);
        context.removeNode(nodeId);
        notify();
    }

    @Override
    public String toString() {
        return "RemoveNodeTask{" +
                "nodeId=" + nodeId +
                '}';
    }

}
