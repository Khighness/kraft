package top.parak.kraft.core.log.sequence;

import org.junit.Assert;
import org.junit.Test;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryFactory;
import top.parak.kraft.core.log.entry.GeneralEntry;
import top.parak.kraft.core.log.entry.NoOpEntry;
import top.parak.kraft.core.support.file.ByteArraySeekableFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EntriesFileTest {

    @Test
    public void testAppendEntry() throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        EntriesFile entriesFile = new EntriesFile(seekableFile);
        Assert.assertEquals(0L, entriesFile.appendEntry(new NoOpEntry(2, 3)));

        seekableFile.seek(0);
        Assert.assertEquals(Entry.KIND_NO_OP, seekableFile.readInt()); // kind
        Assert.assertEquals(2, seekableFile.readInt());       // index
        Assert.assertEquals(3, seekableFile.readInt());       // term
        Assert.assertEquals(0, seekableFile.readInt());       // command bytes length
        Assert.assertEquals(16, seekableFile.size());

        byte[] commandBytes = "test".getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(16L, entriesFile.appendEntry(new GeneralEntry(3, 3, commandBytes)));
        seekableFile.seek(16L);
        Assert.assertEquals(Entry.KIND_GENERAL, seekableFile.readInt());   // kind
        Assert.assertEquals(3, seekableFile.readInt());           // index
        Assert.assertEquals(3, seekableFile.readInt());           // term
        Assert.assertEquals(commandBytes.length, seekableFile.readInt());  // command bytes length
        byte[] buffer = new byte[4];
        seekableFile.read(buffer);
        Assert.assertArrayEquals(commandBytes, buffer);
    }

    @Test
    public void testLoadEntry() throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        EntriesFile entriesFile = new EntriesFile(seekableFile);
        Assert.assertEquals(0L, entriesFile.appendEntry(new NoOpEntry(2, 3)));
        Assert.assertEquals(16L, entriesFile.appendEntry(new GeneralEntry(3, 3, "test".getBytes())));
        Assert.assertEquals(36L, entriesFile.appendEntry(new GeneralEntry(4, 3, "foo".getBytes())));

        EntryFactory entryFactory = new EntryFactory();
        Entry entry = entriesFile.loadEntry(0L, entryFactory);
        Assert.assertEquals(Entry.KIND_NO_OP, entry.getKind());
        Assert.assertEquals(2, entry.getIndex());
        Assert.assertEquals(3, entry.getTerm());

        entry = entriesFile.loadEntry(36L, entryFactory);
        Assert.assertEquals(Entry.KIND_GENERAL, entry.getKind());
        Assert.assertEquals(4, entry.getIndex());
        Assert.assertEquals(3, entry.getTerm());
        Assert.assertArrayEquals("foo".getBytes(), entry.getCommandBytes());
    }

    @Test
    public void testTruncate() throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        EntriesFile entriesFile = new EntriesFile(seekableFile);
        entriesFile.appendEntry(new NoOpEntry(2, 3));
        Assert.assertTrue(seekableFile.size() > 0);
        entriesFile.truncate(0L);
        Assert.assertEquals(0L, seekableFile.size());
    }

}
