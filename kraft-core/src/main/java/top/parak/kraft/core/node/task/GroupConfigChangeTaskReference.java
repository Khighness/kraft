package top.parak.kraft.core.node.task;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeoutException;

/**
 * Task reference for {@link GroupConfigChangeTask}.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public interface GroupConfigChangeTaskReference {

    /**
     * Wait for result forever.
     *
     * @return result
     * @throws InterruptedException if interrupted
     */
    @Nonnull
    GroupConfigChangeTaskResult getResult() throws InterruptedException;

    /**
     * Wait for result in specified timeout.
     *
     * @param timeout timeout
     * @return result
     * @throws InterruptedException if interrupted
     * @throws TimeoutException if timeout
     */
    @Nonnull
    GroupConfigChangeTaskResult getResult(long timeout) throws InterruptedException, TimeoutException;

    /**
     * Cancel task.
     */
    void cancel();

}
