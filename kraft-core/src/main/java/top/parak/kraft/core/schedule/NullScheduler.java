package top.parak.kraft.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Null scheduler.
 * <p>
 * Used to test.
 * </p>
 *
 * @author KHighness
 * @email parakovo@gmail.com
 * @since 2022-03-18
 */
public class NullScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(NullScheduler.class);

    @Override
    @Nonnull
    public LogReplicationTask scheduleLogReplicationTask(@Nonnull Runnable task) {
        logger.debug("schedule log replication task");
        return LogReplicationTask.NONE;
    }

    @Override
    @Nonnull
    public ElectionTimeout scheduleElectionTimeout(@Nonnull Runnable task) {
        logger.debug("schedule election timeout");
        return ElectionTimeout.NONE;
    }

    @Override
    public void stop() throws InterruptedException {
    }

}
