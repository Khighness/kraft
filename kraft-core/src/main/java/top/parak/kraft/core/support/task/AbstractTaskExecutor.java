package top.parak.kraft.core.support.task;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * Abstract task executor.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public abstract class AbstractTaskExecutor implements TaskExecutor {

    @Override
    public void submit(@Nonnull Runnable task, @Nonnull FutureCallback<Object> callback) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(callback);
        submit(task, Collections.singletonList(callback));
    }

}
