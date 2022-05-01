package top.parak.kraft.core.schedule;

import com.sun.istack.internal.NotNull;

import javax.annotation.Nonnull;

/**
 * Scheduler.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public interface Scheduler {

    /**
     * Schedule log replication task.
     *
     * @param task task
     * @return log replication task
     */
    @NotNull
    LogReplicationTask scheduleLogReplicationTask(@Nonnull Runnable task);

    /**
     * Schedule election timeout.
     *
     * @param task task
     * @return election timeout
     */
    @Nonnull
    ElectionTimeout scheduleElectionTimeout(@Nonnull Runnable task);

    /**
     * Stop scheduler.
     *
     * @throws InterruptedException if interrupted
     */
    void stop() throws InterruptedException;

}
