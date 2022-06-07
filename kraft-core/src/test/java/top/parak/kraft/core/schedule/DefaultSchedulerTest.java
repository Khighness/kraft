package top.parak.kraft.core.schedule;

import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class DefaultSchedulerTest {

    @Test
    public void testScheduleFuture() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DefaultScheduler defaultScheduler = new DefaultScheduler(100, 1000, 100, 1000);
        ElectionTimeout e = defaultScheduler.scheduleElectionTimeout(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
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

    @Test
    public void ipdateJavaDoc() throws IOException {
        File file = new File("");
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        File newFile = new File("");
        if  (newFile.createNewFile()) {
            FileOutputStream outputStream = new FileOutputStream(newFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            String line;
            int n = 0;
            while ((line = bufferedReader.readLine()) != null) {
                n++;
                // 16 <= n <= 407
                if (16 <= n && n <= 407 && n % 2 == 1) {
                    continue;
                }
                bufferedWriter.write(line + "\n");
            }

            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

}
