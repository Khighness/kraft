package top.parak.kraft.core.node.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.log.entry.AddNodeEntry;
import top.parak.kraft.core.log.entry.GroupConfigEntry;
import top.parak.kraft.core.log.entry.RemoveNodeEntry;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.TimeoutException;

/**
 * Holder for {@link GroupConfigChangeTask}.
 *
 * @author KHighness
 * @since 2022-06-02
 * @email parakovo@gmail.com
 */
@Immutable
public class GroupConfigChangeTaskHolder {

    private static final Logger logger = LoggerFactory.getLogger(GroupConfigChangeTaskHolder.class);
    private final GroupConfigChangeTask task;
    private final GroupConfigChangeTaskReference reference;

    public GroupConfigChangeTaskHolder() {
        this(GroupConfigChangeTask.NONE, new FixedResultGroupConfigTaskReference(GroupConfigChangeTaskResult.OK));
    }

    public GroupConfigChangeTaskHolder(GroupConfigChangeTask task, GroupConfigChangeTaskReference reference) {
        this.task = task;
        this.reference = reference;
    }

    public void awaitDone(long timeout) throws TimeoutException, InterruptedException {
        if (timeout == 0) {
            reference.getResult();
        } else {
            reference.getResult(timeout);
        }
    }

    public void cancel() {
        reference.cancel();
    }

    public boolean isEmpty() {
        return task == GroupConfigChangeTask.NONE;
    }

    public boolean onLogCommitted(GroupConfigEntry entry) {
        if (isEmpty()) {
            return false;
        }
        logger.debug("log {} committed, current task {}", entry, task);
        if (entry instanceof AddNodeEntry && task instanceof AddNodeTask
                && task.isTargetNode(((AddNodeEntry) entry).getNewNodeEndpoint().getId())) {
            task.onLogCommitted();
            return true;
        }
        if (entry instanceof RemoveNodeEntry && task instanceof RemoveNodeTask
                && task.isTargetNode(((RemoveNodeEntry) entry).getNodeToRemove())) {
            task.onLogCommitted();
            return true;
        }
        return false;
    }

}
