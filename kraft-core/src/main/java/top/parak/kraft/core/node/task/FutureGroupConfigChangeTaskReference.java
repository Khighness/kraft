package top.parak.kraft.core.node.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future for {@link GroupConfigChangeTaskReference}.
 *
 * @author KHighness
 * @since 2022-06-01
 * @email parakovo@gmail.com
 */
public class FutureGroupConfigChangeTaskReference implements GroupConfigChangeTaskReference {

    private static final Logger logger = LoggerFactory.getLogger(FutureGroupConfigChangeTaskReference.class);
    private final Future<GroupConfigChangeTaskResult> future;

    public FutureGroupConfigChangeTaskReference(Future<GroupConfigChangeTaskResult> future) {
        this.future = future;
    }

    @Nonnull
    @Override
    public GroupConfigChangeTaskResult getResult() throws InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            logger.warn("task execution failed", e);
            return GroupConfigChangeTaskResult.ERROR;
        }
    }

    @Nonnull
    @Override
    public GroupConfigChangeTaskResult getResult(long timeout) throws InterruptedException, TimeoutException {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            logger.warn("task execution failed", e);
            return GroupConfigChangeTaskResult.ERROR;
        }
    }

    @Override
    public void cancel() {
        future.cancel(true);
    }

}
