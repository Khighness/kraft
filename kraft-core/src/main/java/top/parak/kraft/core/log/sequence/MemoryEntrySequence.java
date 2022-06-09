package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.GroupConfigEntry;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

/**
 * Memory-based log entry sequence.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class MemoryEntrySequence extends AbstractEntrySequence {

    /**
     * The list to store the log entries.
     */
    private final List<Entry> entries = new ArrayList<>();
    /**
     * The initial commitIndex defined in RAFT is {@code 0}, regardless
     * of whether the log is persistent or not.
     */
    public MemoryEntrySequence() {
        this(1);
    }

    /**
     * Create MemoryEntrySequence.
     *
     * @param logIndexOffset the index of the first log entry
     */
    public MemoryEntrySequence(int logIndexOffset) {
        super(logIndexOffset);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex - logIndexOffset, toIndex - logIndexOffset);
    }

    @Override
    protected Entry doGetEntry(int index) {
        return entries.get(index - logIndexOffset);
    }

    @Override
    protected void doAppend(Entry entry) {
        entries.add(entry);
    }

    @Override
    public void commit(int index) {
        // TODO
    }

    @Override
    public int getCommitIndex() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public GroupConfigEntryList buildGroupConfigEntryList() {
        GroupConfigEntryList list = new GroupConfigEntryList();
        for (Entry entry : entries) {
            if (entry instanceof GroupConfigEntry) {
                list.add((GroupConfigEntry) entry);
            }
        }
        return list;
    }

    @Override
    protected void doRemoveAfter(int index) {
        if (index < doGetFirstLogIndex()) {
            entries.clear();
            nextLogIndex = logIndexOffset;
        } else {
            entries.subList(index - logIndexOffset + 1, entries.size()).clear();
            nextLogIndex = index + 1;
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return "MemoryEntrySequence{" +
                "logIndexOffset=" + logIndexOffset +
                ", nextLogIndex=" + nextLogIndex +
                ", entries.size=" + entries.size() +
                '}';
    }

}
