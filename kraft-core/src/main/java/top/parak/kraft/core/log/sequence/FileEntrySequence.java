package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.LogDir;
import top.parak.kraft.core.log.LogException;
import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryFactory;
import top.parak.kraft.core.log.entry.EntryMeta;
import top.parak.kraft.core.log.entry.GroupConfigEntry;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * File-based log entry sequence.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class FileEntrySequence extends AbstractEntrySequence {

    /**
     * The factory to create a log entry.
     */
    private final EntryFactory entryFactory = new EntryFactory();
    /**
     * The file to store the log entries.
     */
    private final EntriesFile entriesFile;
    /**
     * The file to store the log entry index.
     */
    private final EntryIndexFile entryIndexFile;
    /**
     * The list to cache the log entries.
     */
    private final LinkedList<Entry> pendingEntries = new LinkedList<>();
    /**
     * The initial commitIndex defined in RAFT is {@code 0}, regardless
     * of whether the log is persistent or not.
     */
    private int commitIndex = 0;

    /**
     * Create FileEntrySequence.
     *
     * @param logDir         the log dir
     * @param logIndexOffset the index of the first log entry
     */
    public FileEntrySequence(LogDir logDir, int logIndexOffset) {
        super(logIndexOffset);
        try {
            this.entriesFile = new EntriesFile(logDir.getEntriesFile());
            this.entryIndexFile = new EntryIndexFile(logDir.getEntryOffsetIndexFile());
            initialize();
        } catch (IOException e) {
            throw new LogException("failed to open entries file of entry index file", e);
        }
    }

    /**
     * Create FileEntrySequence.
     *
     * @param entriesFile    the log entry file
     * @param entryIndexFile the log entry index file
     * @param logIndexOffset the index of the first log entry
     */
    public FileEntrySequence(EntriesFile entriesFile, EntryIndexFile entryIndexFile, int logIndexOffset) {
        super(logIndexOffset);
        this.entriesFile = entriesFile;
        this.entryIndexFile = entryIndexFile;
        initialize();
    }

    /**
     * Initialize fields.
     */
    private void initialize() {
        if (entryIndexFile.isEmpty()) {
            commitIndex = logIndexOffset - 1;
            return;
        }
        logIndexOffset = entryIndexFile.getMinEntryIndex();
        nextLogIndex = entryIndexFile.getMaxEntryIndex() + 1;
        commitIndex = entryIndexFile.getMaxEntryIndex();
    }

    /**
     * Get the log entry whose index equals to the specified index from the log entry file.
     *
     * @param index the specified index
     * @return the log entry
     */
    private Entry getEntryInFile(int index) {
        long offset = entryIndexFile.getOffset(index);
        try {
            return entriesFile.loadEntry(offset, entryFactory);
        } catch (IOException e) {
            throw new LogException("failed to load entry " + index, e);
        }
    }

    @Override
    protected Entry doGetEntry(int index) {
        // search in cache
        if (!pendingEntries.isEmpty()) {
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex) {
                return pendingEntries.get(index - firstPendingEntryIndex);
            }
        }
        // search in file
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(index);
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        if (entryIndexFile.isEmpty()) {
            return pendingEntries.get(index - doGetFirstLogIndex()).getMeta();
        }
        return entryIndexFile.get(index).toEntryMeta();
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        List<Entry> result = new ArrayList<>();

        // entries from file
        if (!entryIndexFile.isEmpty() && fromIndex <= entryIndexFile.getMaxEntryIndex()) {
            int maxIndex = Math.min(entryIndexFile.getMaxEntryIndex() + 1, toIndex);
            for (int i = fromIndex; i < maxIndex; i++) {
                result.add(getEntryInFile(i));
            }
        }

        // entries from cache
        if (!pendingEntries.isEmpty() && toIndex > pendingEntries.getFirst().getIndex()) {
            Iterator<Entry> iterator = pendingEntries.iterator();
            Entry entry;
            int index;
            while (iterator.hasNext()) {
                entry = iterator.next();
                index = entry.getIndex();
                if (index >= toIndex) {
                    break;
                }
                if (index >= fromIndex) {
                    result.add(entry);
                }
            }
        }

        return result;
    }

    @Override
    public Entry getLastEntry() {
        if (isEmpty()) {
            return null;
        }
        if (!pendingEntries.isEmpty()) {
            return pendingEntries.getLast();
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(entryIndexFile.getMaxEntryIndex());
    }

    @Override
    protected void doAppend(Entry entry) {
        pendingEntries.add(entry);
    }

    @Override
    protected void doRemoveAfter(int index) {
        // remove entries in cache
        if (!pendingEntries.isEmpty() && index >= pendingEntries.getFirst().getIndex() - 1) {
            for (int i = index + 1; i < doGetLastLogIndex(); i++) {
                pendingEntries.removeLast();
            }
            nextLogIndex = index + 1;
            return;
        }
        // remove entries in file
        try {
            // index >= index of first log entry in file
            // remove entries whose index is greater than index
            if (index >= doGetFirstLogIndex()) {
                pendingEntries.clear();
                entriesFile.truncate(entryIndexFile.getOffset(index + 1));
                entryIndexFile.removeAfter(index);
                nextLogIndex = index + 1;
                commitIndex = index;
            }
            // index < index of first log entry in file
            // clear the log entry file and the log entry index file
            else {
                pendingEntries.clear();
                entriesFile.clear();
                entryIndexFile.clear();
                nextLogIndex = logIndexOffset;
                commitIndex = logIndexOffset - 1;
            }
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    @Override
    public void commit(int index) {
        if (index < commitIndex) {
            throw new IllegalArgumentException("commit index < " + commitIndex);
        }
        if (index == commitIndex) {
            return;
        }
        if (pendingEntries.isEmpty() || pendingEntries.getLast().getIndex() < index) {
            throw new IllegalArgumentException("no entry to commit or commit index exceed");
        }
        // move entries from cache to file
        long offset;
        Entry entry = null;
        try {
            for (int i = commitIndex + 1; i <= index; i++) {
                entry = pendingEntries.removeFirst();
                offset = entriesFile.appendEntry(entry);
                entryIndexFile.appendEntryIndex(i, offset, entry.getKind(), entry.getTerm());
                commitIndex = i;
            }
        } catch (IOException e) {
            throw new LogException("failed to commit entry " + entry, e);
        }
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {
        try {
            entriesFile.close();
            entryIndexFile.clear();
        } catch (IOException e) {
            throw new LogException("failed to close", e);
        }
    }

    @Override
    public GroupConfigEntryList buildGroupConfigEntryList() {
        GroupConfigEntryList list = new GroupConfigEntryList();

        // check file
        try {
            int entryKind;
            for (EntryIndexItem entryIndexItem : entryIndexFile) {
                entryKind = entryIndexItem.getKind();
                if (entryKind == Entry.KIND_ADD_NODE || entryKind == Entry.KIND_REMOVE_NODE) {
                    list.add((GroupConfigEntry) entriesFile.loadEntry(entryIndexItem.getOffset(), entryFactory));
                }
            }
        } catch (IOException e) {
            throw new LogException("failed to load entry", e);
        }

        // check cache
        pendingEntries.stream()
                .filter(GroupConfigEntry.class::isInstance)
                .forEach(entry -> list.add((GroupConfigEntry) entry));

        return list;
    }

}
