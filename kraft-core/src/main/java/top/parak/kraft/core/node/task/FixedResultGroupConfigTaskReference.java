package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.task.GroupConfigChangeTaskReference;
import top.parak.kraft.core.node.task.GroupConfigChangeTaskResult;

import javax.annotation.Nonnull;

public class FixedResultGroupConfigTaskReference implements GroupConfigChangeTaskReference {

    private final GroupConfigChangeTaskResult result;

    public FixedResultGroupConfigTaskReference(GroupConfigChangeTaskResult result) {
        this.result = result;
    }

    @Override
    @Nonnull
    public GroupConfigChangeTaskResult getResult() throws InterruptedException {
        return result;
    }

    @Override
    @Nonnull
    public GroupConfigChangeTaskResult getResult(long timeout) {
        return result;
    }

    @Override
    public void cancel() {
    }

}
