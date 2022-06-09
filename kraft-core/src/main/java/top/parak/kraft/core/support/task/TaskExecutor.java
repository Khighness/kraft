package top.parak.kraft.core.support.task;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Task executor.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public interface TaskExecutor {

    /**
     * Submit task.
     *
     * @param task task
     * @return future
     */
    @Nonnull
    Future<?> submit(@Nonnull Runnable task);

    /**
     * Submit callable task.
     *
     * @param task task
     * @param <V>  result type
     * @return future
     */
    @Nonnull
    <V> Future<V> submit(@Nonnull Callable<V> task);

    /**
     * Submit task with callback.
     *
     * @param task     task
     * @param callback callback
     */
    void submit(@Nonnull Runnable task, @Nonnull FutureCallback<Object> callback);

    /**
     * Submit task with callbacks.
     *
     * @param task task
     * @param callbacks callbacks, should not be empty
     */
    void submit(@Nonnull Runnable task, @Nonnull Collection<FutureCallback<Object>> callbacks);

    /**
     * Shutdown.
     *
     * @throws InterruptedException if interrupted
     */
    void shutdown() throws InterruptedException;

}
