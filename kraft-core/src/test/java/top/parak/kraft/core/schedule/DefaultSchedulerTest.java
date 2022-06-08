package top.parak.kraft.core.schedule;

import org.junit.BeforeClass;
import org.junit.Test;
import top.parak.kraft.core.node.NodeBuilder;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.node.NodeImpl;
import top.parak.kraft.core.support.task.ListeningTaskExecutor;
import top.parak.kraft.core.support.task.SingleThreadTaskExecutor;
import top.parak.kraft.core.support.task.TaskExecutor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static TaskExecutor taskExecutor;
    private static TaskExecutor groupConfigChangeTaskExecutor;
    private static TaskExecutor cachedThreadTaskExecutor;
    private static final AtomicInteger cachedThreadId = new AtomicInteger(0);

    @BeforeClass
    public static void beforeClass() {
        taskExecutor = new SingleThreadTaskExecutor("node-test");
        groupConfigChangeTaskExecutor = new SingleThreadTaskExecutor("group-config-change-test");
        cachedThreadTaskExecutor = new ListeningTaskExecutor(Executors.newCachedThreadPool(r ->
                new Thread(r, "cached-thread-" + cachedThreadId.incrementAndGet())));
    }

    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... endpoints) {
        return new NodeBuilder(Arrays.asList(endpoints), selfId);
    }


    @Test
    public void testTwoNode() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<NodeImpl> future1 = executorService.submit(() -> {
            NodeImpl node = (NodeImpl) newNodeBuilder(
                    NodeId.of("A"),
                    new NodeEndpoint("A", "127.0.0.1", 2333),
                    new NodeEndpoint("B", "127.0.0.1", 2334))
                    .build();
            node.start();

            return node;
        });
        Future<NodeImpl> future2 = executorService.submit(() -> {
            NodeImpl node = (NodeImpl) newNodeBuilder(
                    NodeId.of("B"),
                    new NodeEndpoint("A", "127.0.0.1", 2333),
                    new NodeEndpoint("B", "127.0.0.1", 2334))
                    .build();
            node.start();

            return node;
        });

        NodeImpl a = future1.get();
        NodeImpl b = future2.get();
    }

}
