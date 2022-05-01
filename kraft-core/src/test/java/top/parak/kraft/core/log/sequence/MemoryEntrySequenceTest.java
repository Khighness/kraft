package top.parak.kraft.core.log.sequence;

import org.junit.Assert;
import org.junit.Test;

import top.parak.kraft.core.log.entry.*;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MemoryEntrySequenceTest {

    @Test
    public void testAppendEntry() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence();
        memoryEntrySequence.append(new NoOpEntry(memoryEntrySequence.getNextLogIndex(), 1));
        Assert.assertEquals(2, memoryEntrySequence.getNextLogIndex());
        Assert.assertEquals(1, memoryEntrySequence.getLastLogIndex());
    }

    @Test
    public void testAppendEntries() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence();
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(1, 1),
                new NoOpEntry(2, 1)
        ));
        Assert.assertEquals(3, memoryEntrySequence.getNextLogIndex());
        Assert.assertEquals(2, memoryEntrySequence.getLastLogIndex());
    }

    @Test
    public void testGetEntry() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        Assert.assertNull(memoryEntrySequence.getEntry(1));
        Assert.assertEquals(2, memoryEntrySequence.getEntry(2).getIndex());
        Assert.assertEquals(3, memoryEntrySequence.getEntry(3).getIndex());
        Assert.assertNull(memoryEntrySequence.getEntry(4));
    }

    @Test
    public void testGetEntryMeta() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        Assert.assertNull(memoryEntrySequence.getEntry(2));
        memoryEntrySequence.append(new NoOpEntry(2, 1));
        EntryMeta meta = memoryEntrySequence.getEntryMeta(2);
        Assert.assertNotNull(meta);
        Assert.assertEquals(2, meta.getIndex());
        Assert.assertEquals(1, meta.getTerm());
    }

    @Test
    public void testIsEntryPresent() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(1);
        Assert.assertFalse(memoryEntrySequence.isEntryPresent(1));
        memoryEntrySequence.append(new NoOpEntry(1, 1));
        Assert.assertTrue(memoryEntrySequence.isEntryPresent(1));
        Assert.assertFalse(memoryEntrySequence.isEntryPresent(0));
        Assert.assertFalse(memoryEntrySequence.isEntryPresent(2));
    }

    @Test(expected = EmptySequenceException.class)
    public void testSubListEmpty() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        Assert.assertTrue(memoryEntrySequence.subList(2, 2).isEmpty());
    }

    @Test
    public void testSubListResultEmpty() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(new NoOpEntry(2, 1));
        memoryEntrySequence.subList(2, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubListOutOfIndex() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(new NoOpEntry(2, 1));
        memoryEntrySequence.subList(1, 3);
    }

    @Test
    public void testSubListOneElement() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = memoryEntrySequence.subList(2, 3);
        Assert.assertEquals(1, subList.size());
        Assert.assertEquals(2, subList.get(0).getIndex());
    }

    @Test
    public void testSubViewEmpty() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        Assert.assertTrue(memoryEntrySequence.subView(2).isEmpty());
    }

    @Test
    public void testSubView() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = memoryEntrySequence.subView(2);
        Assert.assertEquals(2, subList.size());
        Assert.assertEquals(2, subList.get(0).getIndex());
        Assert.assertEquals(3, subList.get(1).getIndex());
    }

    @Test
    public void testSubView2() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = memoryEntrySequence.subView(4);
        Assert.assertEquals(0, subList.size());
    }

    @Test
    public void testSubView3() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = memoryEntrySequence.subView(1);
        Assert.assertEquals(2, subList.size());
    }

    @Test
    public void testBuildGroupConfigList() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new AddNodeEntry(2, 1, Collections.emptySet(), new NodeEndpoint("A", "localhost", 2333)),
                new NoOpEntry(3, 1),
                new RemoveNodeEntry(4, 1, Collections.emptySet(), new NodeId("A"))
        ));
        GroupConfigEntryList list = memoryEntrySequence.buildGroupConfigEntryList();
        Iterator<GroupConfigEntry> iterator = list.iterator();
        Assert.assertEquals(2, iterator.next().getIndex());
        Assert.assertEquals(4, iterator.next().getIndex());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testRemoveAfterEmpty() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence();
        memoryEntrySequence.removeAfter(1);
    }

    @Test
    public void testRemoveAfterNoAction() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        memoryEntrySequence.removeAfter(3);
        Assert.assertEquals(3, memoryEntrySequence.getLastLogIndex());
        Assert.assertEquals(4, memoryEntrySequence.getNextLogIndex());
    }

    @Test
    public void testRemoveAfterPartial() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        memoryEntrySequence.removeAfter(2);
        Assert.assertEquals(2, memoryEntrySequence.getLastLogIndex());
        Assert.assertEquals(3, memoryEntrySequence.getNextLogIndex());
    }

    @Test
    public void testRemoveAfterAll() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        Assert.assertNotNull(memoryEntrySequence.getEntry(2));
        memoryEntrySequence.removeAfter(1);
        Assert.assertTrue(memoryEntrySequence.isEmpty());
        Assert.assertEquals(2, memoryEntrySequence.getNextLogIndex());
    }

}