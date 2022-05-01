package top.parak.kraft.core.log.sequence;

import org.junit.Assert;
import org.junit.Test;

import top.parak.kraft.core.support.file.ByteArraySeekableFile;

import java.io.IOException;
import java.util.Iterator;

public class EntryIndexFileTest {

    @Test
    public void testLoadEmpty() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
        Assert.assertTrue(entryIndexFile.isEmpty());
        Assert.assertEquals(0, entryIndexFile.getEntryIndexCount());
    }

    @Test
    public void testLoad() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(3, 4);

        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);
        Assert.assertEquals(3, entryIndexFile.getMinEntryIndex());
        Assert.assertEquals(4, entryIndexFile.getMaxEntryIndex());
        Assert.assertEquals(2, entryIndexFile.getEntryIndexCount());

        EntryIndexItem item = entryIndexFile.get(3);
        Assert.assertNotNull(item);
        Assert.assertEquals(30L, item.getOffset());
        Assert.assertEquals(1, item.getKind());
        Assert.assertEquals(3, item.getTerm());

        item = entryIndexFile.get(4);
        Assert.assertNotNull(item);
        Assert.assertEquals(40L, item.getOffset());
        Assert.assertEquals(1, item.getKind());
        Assert.assertEquals(4, item.getTerm());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetMinEntryIndexEmpty() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
        entryIndexFile.getMinEntryIndex();
    }

    @Test
    public void testAppendEntryIndex() throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);

        // append when empty
        entryIndexFile.appendEntryIndex(10, 100L, 1, 2);
        Assert.assertEquals(1, entryIndexFile.getEntryIndexCount());
        Assert.assertEquals(10, entryIndexFile.getMinEntryIndex());
        Assert.assertEquals(10, entryIndexFile.getMaxEntryIndex());

        // check entryIndexFile content
        seekableFile.seek(0L);
        Assert.assertEquals(10, seekableFile.readInt()); // min entry index
        Assert.assertEquals(10, seekableFile.readInt()); // max entry index
        Assert.assertEquals(100L, seekableFile.readLong()); // offset
        Assert.assertEquals(1, seekableFile.readInt()); // kind
        Assert.assertEquals(2, seekableFile.readInt()); // term

        EntryIndexItem item = entryIndexFile.get(10);
        Assert.assertNotNull(item);
        Assert.assertEquals(100L, item.getOffset());
        Assert.assertEquals(1, item.getKind());
        Assert.assertEquals(2, item.getTerm());

        // append when not empty
        entryIndexFile.appendEntryIndex(11, 200L, 1, 2);
        Assert.assertEquals(2, entryIndexFile.getEntryIndexCount());
        Assert.assertEquals(10, entryIndexFile.getMinEntryIndex());
        Assert.assertEquals(11, entryIndexFile.getMaxEntryIndex());

        // check entryIndexFile content
        seekableFile.seek(24L); // skip min/max and first entry index
        Assert.assertEquals(200L, seekableFile.readLong()); // offset
        Assert.assertEquals(1, seekableFile.readInt()); // kind
        Assert.assertEquals(2, seekableFile.readInt()); // term
    }

    private ByteArraySeekableFile makeEntryIndexFileContent(int minEntryIndex, int maxEntryIndex) throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        seekableFile.writeInt(minEntryIndex);
        seekableFile.writeInt(maxEntryIndex);
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            seekableFile.writeLong(10L * i); // offset
            seekableFile.writeInt(1); // kind
            seekableFile.writeInt(i); // term
        }
        seekableFile.seek(0L);
        return seekableFile;
    }

    @Test
    public void testClear() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);
        Assert.assertFalse(entryIndexFile.isEmpty());
        entryIndexFile.clear();
        Assert.assertTrue(entryIndexFile.isEmpty());
        Assert.assertEquals(0, entryIndexFile.getEntryIndexCount());
        Assert.assertEquals(0L, seekableFile.size());
    }

    @Test
    public void testRemoveAfterEmpty() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
        entryIndexFile.removeAfter(100);
    }

    @Test
    public void testRemoveAfter() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        long oldSize = seekableFile.size();
        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);
        entryIndexFile.removeAfter(6);
        Assert.assertEquals(5, entryIndexFile.getMinEntryIndex());
        Assert.assertEquals(6, entryIndexFile.getMaxEntryIndex());
        Assert.assertEquals(oldSize, seekableFile.size());
        Assert.assertEquals(2, entryIndexFile.getEntryIndexCount());
    }

    @Test
    public void testRemoveAfterOne() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        long oldSize = seekableFile.size();
        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);
        entryIndexFile.removeAfter(5);
        Assert.assertEquals(5, entryIndexFile.getMinEntryIndex());
        Assert.assertEquals(5, entryIndexFile.getMaxEntryIndex());

        // one item removed
        Assert.assertEquals(oldSize - 16, seekableFile.size());
        Assert.assertEquals(1, entryIndexFile.getEntryIndexCount());
    }

    @Test
    public void testRemoveAfterAll() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile entryIndexFile = new EntryIndexFile(seekableFile);
        entryIndexFile.removeAfter(4);

        // all removed
        Assert.assertEquals(0L, seekableFile.size());
        Assert.assertTrue(entryIndexFile.isEmpty());
        Assert.assertEquals(0, entryIndexFile.getEntryIndexCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetEmpty() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
        entryIndexFile.get(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLessThanMin() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        entryIndexFile.get(2);
    }

    @Test
    public void testGet() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        EntryIndexItem item = entryIndexFile.get(3);
        Assert.assertNotNull(item);
        Assert.assertEquals(1, item.getKind());
        Assert.assertEquals(3, item.getTerm());
    }

    @Test
    public void testIteratorEmpty() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
        Iterator<EntryIndexItem> iterator = entryIndexFile.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterator() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        Iterator<EntryIndexItem> iterator = entryIndexFile.iterator();
        Assert.assertTrue(iterator.hasNext());
        EntryIndexItem item = iterator.next();
        Assert.assertEquals(3, item.getIndex());
        Assert.assertEquals(1, item.getKind());
        Assert.assertEquals(3, item.getTerm());
        Assert.assertTrue(iterator.hasNext());
        item = iterator.next();
        Assert.assertEquals(4, item.getIndex());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorModification() throws IOException {
        EntryIndexFile entryIndexFile = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        Iterator<EntryIndexItem> iterator = entryIndexFile.iterator();
        entryIndexFile.removeAfter(3);
        iterator.next();
    }
    
}