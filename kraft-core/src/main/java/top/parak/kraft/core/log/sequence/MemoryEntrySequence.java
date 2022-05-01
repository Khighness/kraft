package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.GroupConfigEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Memory-based log entry sequence.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
public class MemoryEntrySequence extends AbstractEntrySequence {

    /**
     * The list to store the log entries.
     */
    private final List<Entry> entries = new ArrayList<>();
    /**
     * The initial commitIndex defined in RAFT is {@code 0}, regardless
     * of whether the log is persistent or not.
     */
    private int commitIndex = 0;

    public MemoryEntrySequence() {
        super(1);
    }

    public MemoryEntrySequence(int logIndexOffset) {
        super(logIndexOffset);
    }

    @Override
    protected Entry doGetEntry(int index) {
        return entries.get(index - logIndexOffset);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex - logIndexOffset, toIndex - logIndexOffset);
    }

    @Override
    protected void doAppend(Entry entry) {
        entries.add(entry);
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
    public GroupConfigEntryList buildGroupConfigEntryList() {
        GroupConfigEntryList list = new GroupConfigEntryList();
        entries.stream()
                .filter(entry -> entry instanceof GroupConfigEntry)
                .forEach(entry -> {
                   list.add((GroupConfigEntry) entry);
                });
        return list;
    }

    @Override
    public void commit(int index) {
        this.commitIndex = index;
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {
        // it seems nothing to do
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
