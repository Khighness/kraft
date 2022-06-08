package top.parak.kraft.core.log.sequence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import top.parak.kraft.core.log.entry.*;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.support.file.ByteArraySeekableFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FileEntrySequenceTest {

    private EntriesFile entriesFile;
    private EntryIndexFile entryIndexFile;

    @Before
    public void setUp() throws IOException {
        entriesFile = new EntriesFile(new ByteArraySeekableFile());
        entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
    }

    @Test
    public void testInitializeEmpty() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 5);
        Assert.assertEquals(5, fileEntrySequence.getNextLogIndex());
        Assert.assertTrue(fileEntrySequence.isEmpty());
    }

    @Test
    public void testInitialize() throws IOException {
        entryIndexFile.appendEntryIndex(1, 0L, 1, 1);
        entryIndexFile.appendEntryIndex(2, 20L, 1, 1);

        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertEquals(3, fileEntrySequence.getNextLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(2, fileEntrySequence.getLastLogIndex());
        Assert.assertEquals(2, fileEntrySequence.getCommitIndex());
    }

    @Test
    public void testBuildGroupConfigEntryListFromFile() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new GeneralEntry(2, 1, new byte[0]));
        appendEntryToFile(new AddNodeEntry(3, 1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 3331)));
        appendEntryToFile(new RemoveNodeEntry(4, 1, Collections.emptySet(), new NodeId("A")));

        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 5);
        GroupConfigEntryList list = fileEntrySequence.buildGroupConfigEntryList();
        Iterator<GroupConfigEntry> iterator = list.iterator();
        Assert.assertEquals(3, iterator.next().getIndex());
        Assert.assertEquals(4, iterator.next().getIndex());
        Assert.assertFalse(iterator.hasNext());
    }

    private void appendEntryToFile(Entry entry) throws IOException {
        long offset = entriesFile.appendEntry(entry);
        entryIndexFile.appendEntryIndex(entry.getIndex(), offset, entry.getKind(), entry.getTerm());
    }

    @Test
    public void testBuildGroupConfigEntryListFromPendingEntries() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        fileEntrySequence.append(new GeneralEntry(2, 1, new byte[0]));
        fileEntrySequence.append(new AddNodeEntry(3, 1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 3331)));
        fileEntrySequence.append(new RemoveNodeEntry(4, 1, Collections.emptySet(), new NodeId("A")));
        GroupConfigEntryList list = fileEntrySequence.buildGroupConfigEntryList();
        Iterator<GroupConfigEntry> iterator = list.iterator();
        Assert.assertEquals(3, iterator.next().getIndex());
        Assert.assertEquals(4, iterator.next().getIndex());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testBuildGroupConfigEntryListBoth() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new AddNodeEntry(2, 1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 2333)));
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        sequence.append(new RemoveNodeEntry(3, 1, Collections.emptySet(), new NodeId("A")));
        GroupConfigEntryList list = sequence.buildGroupConfigEntryList();
        Iterator<GroupConfigEntry> iterator = list.iterator();
        Assert.assertEquals(2, iterator.next().getIndex());
        Assert.assertEquals(3, iterator.next().getIndex());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSublist1() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subView(1);
        Assert.assertEquals(4, subList.size());
        Assert.assertEquals(1, subList.get(0).getIndex());
        Assert.assertEquals(4, subList.get(3).getIndex());
    }

    @Test
    public void testSublist2() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subView(2);
        Assert.assertEquals(3, subList.size());
        Assert.assertEquals(2, subList.get(0).getIndex());
        Assert.assertEquals(4, subList.get(2).getIndex());
    }

    @Test
    public void testSublist3() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subView(3);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals(3, subList.get(0).getIndex());
        Assert.assertEquals(4, subList.get(1).getIndex());
    }

    @Test
    public void testSublist4() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subView(4);
        Assert.assertEquals(1, subList.size());
        Assert.assertEquals(4, subList.get(0).getIndex());
        Assert.assertEquals(4, subList.get(0).getIndex());
    }

    @Test
    public void testSublist5() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subView(5);
        Assert.assertEquals(0, subList.size());
    }

    @Test
    public void testSublist6() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subList(3, 4);
        Assert.assertEquals(1, subList.size());
    }

    @Test
    public void testSublist7() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 3);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 3));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 4));

        List<Entry> subList = fileEntrySequence.subList(1, 3);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals(1, subList.get(0).getIndex());
        Assert.assertEquals(2, subList.get(1).getIndex());
    }

    @Test
    public void testGetEntry() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(2, 1));
        Assert.assertNull(fileEntrySequence.getEntry(0));
        Assert.assertEquals(1, fileEntrySequence.getEntry(1).getIndex());
        Assert.assertEquals(2, fileEntrySequence.getEntry(2).getIndex());
        Assert.assertNull(fileEntrySequence.getEntry(3));
    }

    @Test
    public void testGetEntryMetaNotFound() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertNull(fileEntrySequence.getEntry(2));
    }

    @Test
    public void testGetEntryMetaInPendingEntries() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        EntryMeta entryMeta = fileEntrySequence.getEntryMeta(1);
        Assert.assertNotNull(entryMeta);
        Assert.assertEquals(Entry.KIND_NO_OP, entryMeta.getKind());
        Assert.assertEquals(1, entryMeta.getIndex());
    }

    @Test
    public void testGetEntryMetaInIndexFile() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        EntryMeta entryMeta = fileEntrySequence.getEntryMeta(1);
        Assert.assertNotNull(entryMeta);
        Assert.assertEquals(Entry.KIND_NO_OP, entryMeta.getKind());
        Assert.assertEquals(1, entryMeta.getIndex());
        Assert.assertEquals(1, entryMeta.getTerm());
    }

    @Test
    public void testGetLastEntryEmpty() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertNull(fileEntrySequence.getLastEntry());
    }

    @Test
    public void testGetLastEntryFromFile() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertEquals(1, fileEntrySequence.getLastEntry().getIndex());
    }

    @Test
    public void testGetLastEntryFromPendingEntries() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        Assert.assertEquals(1, fileEntrySequence.getLastEntry().getIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendIllegalIndex() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(2, 1));
    }

    @Test
    public void testAppendEntry() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertEquals(1, fileEntrySequence.getNextLogIndex());
        fileEntrySequence.append(new NoOpEntry(1, 1));
        Assert.assertEquals(2, fileEntrySequence.getNextLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastEntry().getIndex());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommitBeforeCommitIndex() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.commit(-1);
    }

    @Test
    public void testCommitDoNothing() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.commit(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommitAfterNoPendingEntry() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.commit(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommitAfterLastLogIndex() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        fileEntrySequence.commit(2);
    }

    @Test
    public void testCommitOne() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        fileEntrySequence.append(new NoOpEntry(2, 1));
        Assert.assertEquals(0, fileEntrySequence.getCommitIndex());
        Assert.assertEquals(0, entryIndexFile.getEntryIndexCount());
        fileEntrySequence.commit(1);
        Assert.assertEquals(1, fileEntrySequence.getCommitIndex());
        Assert.assertEquals(1, entryIndexFile.getEntryIndexCount());
        Assert.assertEquals(1, entryIndexFile.getMaxEntryIndex());
    }

    @Test
    public void testCommitMultiple() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        fileEntrySequence.append(new NoOpEntry(2, 1));
        Assert.assertEquals(0, fileEntrySequence.getCommitIndex());
        Assert.assertEquals(0, entryIndexFile.getEntryIndexCount());
        fileEntrySequence.commit(2);
        Assert.assertEquals(2, fileEntrySequence.getCommitIndex());
        Assert.assertEquals(2, entryIndexFile.getEntryIndexCount());
        Assert.assertEquals(2, entryIndexFile.getMaxEntryIndex());
    }

    @Test
    public void testRemoveAfterLargerThanTheLastLogIndex() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(1, 1));
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
        fileEntrySequence.removeAfter(1);
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
    }

    @Test
    public void testRemoveAfterSmallerThanFirstLogIndex1() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 1));
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
        // file: empty
        // cache: 1 pending entry
        fileEntrySequence.removeAfter(0);
        Assert.assertTrue(fileEntrySequence.isEmpty());
    }

    @Test
    public void testRemoveAfterSmallerThanFirstLogIndex2() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
        // file: 1 entry
        // cache: empty
        fileEntrySequence.removeAfter(0);
        Assert.assertTrue(fileEntrySequence.isEmpty());
        Assert.assertEquals(0L, entriesFile.size());
        Assert.assertTrue(entryIndexFile.isEmpty());
    }

    @Test
    public void testRemoveAfterSmallerThanFirstLogIndex3() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(2, 1));
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(2, fileEntrySequence.getLastLogIndex());
        // file: 1 entry
        // cache: 1 pending entry
        fileEntrySequence.removeAfter(0);
        Assert.assertTrue(fileEntrySequence.isEmpty());
        Assert.assertEquals(0L, entriesFile.size());
        Assert.assertTrue(entryIndexFile.isEmpty());
    }

    @Test
    public void testRemoveAfterPendingEntries2() {
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 1));
        fileEntrySequence.append(new NoOpEntry(fileEntrySequence.getNextLogIndex(), 2));
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(2, fileEntrySequence.getLastLogIndex());
        fileEntrySequence.removeAfter(1);
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
    }

    @Test
    public void testRemoveAfterEntriesInFile2() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 1));
        FileEntrySequence fileEntrySequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        fileEntrySequence.append(new NoOpEntry(3, 2)); // 3
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(3, fileEntrySequence.getLastLogIndex());
        fileEntrySequence.removeAfter(1);
        Assert.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assert.assertEquals(1, fileEntrySequence.getLastLogIndex());
    }

}
