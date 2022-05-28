package top.parak.kraft.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.config.NodeConfig;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Default scheduler.
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
@ThreadSafe
public class DefaultScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultScheduler.class);
    private final int minElectionTimeout;
    private final int maxElectionTimeout;
    private final int logReplicationDelay;
    private final int logReplicationInternal;
    private final Random electionTimeoutRandom;
    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultScheduler(NodeConfig config) {
        this(config.getMinElectionTimeout(), config.getMaxElectionTimeout(), config.getLogReplicationDelay(),
                config.getLogReplicationInterval());
    }

    public DefaultScheduler(int minElectionTimeout, int maxElectionTimeout, int logReplicationDelay, int logReplicationInternal) {
        if (minElectionTimeout <= 0 || maxElectionTimeout <= 0 || minElectionTimeout > maxElectionTimeout) {
            throw new IllegalArgumentException("election timeout is negative or zero, or min > max");
        }
        if (logReplicationDelay < 0 || logReplicationInternal <= 0) {
            throw new IllegalArgumentException("log replication delay is negative, or log replication interval is negative or zero");
        }
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInternal = logReplicationInternal;
        this.electionTimeoutRandom = new Random();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "scheduler"));
    }

    @Override
    public LogReplicationTask scheduleLogReplicationTask(@Nonnull Runnable task) {
        logger.debug("schedule log replication task");
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(
                task, logReplicationDelay, logReplicationInternal, TimeUnit.MILLISECONDS
        );
        return new LogReplicationTask(scheduledFuture);
    }

    @Nonnull
    @Override
    public ElectionTimeout scheduleElectionTimeout(@Nonnull Runnable task) {
        logger.debug("schedule election timeout");
        int timeout = electionTimeoutRandom.nextInt(maxElectionTimeout - minElectionTimeout) + minElectionTimeout;
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(task, timeout, TimeUnit.MILLISECONDS);
        return new ElectionTimeout(scheduledFuture);
    }

    @Override
    public void stop() throws InterruptedException {
        logger.debug("stop scheduler");
        scheduledExecutorService.shutdown();
        scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
    }

}
