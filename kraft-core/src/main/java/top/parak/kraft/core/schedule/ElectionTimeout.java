package top.parak.kraft.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Election timeout.
 *
 * @author KHighness
 * @since 2022-03-18
 * @email parakovo@gmail.com
 */
public class ElectionTimeout {

    private static final Logger logger = LoggerFactory.getLogger(ElectionTimeout.class);
    public static final ElectionTimeout NONE = new ElectionTimeout(new NullScheduledFuture());

    private final ScheduledFuture<?> scheduledFuture;

    public ElectionTimeout(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel() {
        logger.debug("cancel election timeout");
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        return "ElectionTimeout{delay=" +
                scheduledFuture.getDelay(TimeUnit.MILLISECONDS) +
                "ms}";
    }

}
