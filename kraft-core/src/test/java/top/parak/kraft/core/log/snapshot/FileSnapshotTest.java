package top.parak.kraft.core.log.snapshot;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.support.file.ByteArraySeekableFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileSnapshotTest {

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileSnapshotWriter writer = new FileSnapshotWriter(output, 1, 2, ImmutableSet.of(
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334)
        ));
        byte[] data = "test".getBytes(StandardCharsets.UTF_8);
        writer.write(data);
        writer.close();

        FileSnapshot snapshot = new FileSnapshot(new ByteArraySeekableFile(output.toByteArray()));
        Assert.assertEquals(1, snapshot.getLastIncludedIndex());
        Assert.assertEquals(2, snapshot.getLastIncludedTerm());
        Assert.assertEquals(2, snapshot.getLastConfig().size());
        Assert.assertEquals(4, snapshot.getDataSize());

        SnapshotChunk snapshotChunk = snapshot.readData(0, 10);
        Assert.assertArrayEquals(data, snapshotChunk.toByteArray());
        Assert.assertTrue(snapshotChunk.isLastChunk());
    }

}
