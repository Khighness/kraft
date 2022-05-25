package top.parak.kraft.core.schedule;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultSchedulerTest {

    @Test
    public void testSchedule() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DefaultScheduler defaultScheduler = new DefaultScheduler(100, 1000, 100, 1000);
        ElectionTimeout e = defaultScheduler.scheduleElectionTimeout(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println("呜呜呜");
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}