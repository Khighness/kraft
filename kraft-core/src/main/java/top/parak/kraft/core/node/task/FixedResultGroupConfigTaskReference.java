package top.parak.kraft.core.node.task;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeoutException;

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

    @Nonnull
    @Override
    public GroupConfigChangeTaskResult getResult() throws InterruptedException {
        return result;
    }

    @Nonnull
    @Override
    public GroupConfigChangeTaskResult getResult(long timeout) throws InterruptedException, TimeoutException {
        return result;
    }

    @Override
    public void cancel() {
    }

}
