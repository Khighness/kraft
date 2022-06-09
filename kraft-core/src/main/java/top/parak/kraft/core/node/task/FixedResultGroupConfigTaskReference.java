package top.parak.kraft.core.node.task;

import javax.annotation.Nonnull;

/**
 * Fixed result for {@link GroupConfigChangeTaskReference}.
 *
 * @author KHighness
 * @since 2022-06-01
 * @email parakovo@gmail.com
 */
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
