package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.support.file.RandomAccessFileAdapter;
import top.parak.kraft.core.support.file.SeekableFile;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Log entry index file.
 *
 * <p><b>Structure of log entry index file</b></p>
 * {@link EntryIndexFile} starts with the start index and end index, followed by
 * mata information of the log entry, including the position offset in the {@link
 * EntriesFile}, the log entry kind and the log entry term.
 * <pre>
 * +---------------+---------------+
 * |     int(4)    |     int(4)    |
 * +---------------+---------------+
 * | minEntryIndex | maxEntryIndex |
 * +-------------------------------+---------------+---------------+
 * |             long(8)           |     int(4)    |     int(4)    |
 * +-------------------------------+---------------+---------------+
 * |             offset            |      kind     |      term     |
 * +-------------------------------+---------------+---------------+
 * |             offset            |      kind     |      term     |
 * +-------------------------------+---------------+---------------+
 * </pre>
 * {@link EntryIndexFile} does not store the log entry index, because the index value
 * can be calculated. For example, the index of the first log entry is {@link #minEntryIndex},
 * then the index of the second log entry is {@code minEntryIndex + 1}, and the index of the
 * last log entry is {@link #maxEntryIndex}.
 *
 * @author KHighness
 * @email parakovo@gmail.com
 * @since 2022-04-02
 */
public class EntryIndexFile implements Iterable<EntryIndexItem> {

    /**
     * The offset of {@link #minEntryIndex} or {@link #maxEntryIndex}.
     */
    private static final long OFFSET_ENTRY_INDEX = Integer.BYTES;
    /**
     * The length of a row of a log entry index.
     */
    private static final int LENGTH_ENTRY_INDEX_ITEM = 16;
    /**
     * The seekable file to store the log entry index.
     */
    private final SeekableFile seekableFile;
    /**
     * The count of the log entries.
     */
    private int entryIndexCount;
    /**
     * The index of the first log entry.
     */
    private int minEntryIndex;
    /**
     * The index of the last log entry.
     */
    private int maxEntryIndex;
    /**
     * The map to store the log entry index as key and {@link EntryIndexItem} as value.
     */
    private Map<Integer, EntryIndexItem> entryIndexMap = new HashMap<>();

    /**
     * Create EntryIndexFile.
     *
     * @param file file
     * @throws IOException if IO exception occurs
     */
    public EntryIndexFile(File file) throws IOException {
        this(new RandomAccessFileAdapter(file));
    }

    /**
     * Create EntryIndexFile.
     *
     * @param seekableFile seekableFile
     * @throws IOException if IO exception occurs
     */
    public EntryIndexFile(SeekableFile seekableFile) throws IOException {
        this.seekableFile = seekableFile;
        load();
    }

    /**
     * Check if index file is empty.
     *
     * @return true if index file is empty, otherwise false
     */
    public boolean isEmpty() {
        return entryIndexCount == 0;
    }

    /**
     * Get min log entry index.
     *
     * @return min entry index
     */
    public int getMinEntryIndex() {
        checkEmpty();
        return minEntryIndex;
    }

    /**
     * Get max log entry index.
     *
     * @return max entry index
     */
    public int getMaxEntryIndex() {
        checkEmpty();
        return maxEntryIndex;
    }

    /**
     * Get the log entry index count.
     *
     * @return entry index count
     */
    public int getEntryIndexCount() {
        return entryIndexCount;
    }

    /**
     * Append a log entry index to the index file.
     *
     * @param index the index of the log entry
     * @param offset the offset of the log entry stored in sequence
     * @param kind the index of the log entry
     * @param term the term of the log entry
     * @throws IOException if IO exception occurs
     * @throws IllegalArgumentException if {@code index != maxEntryIndex + 1}
     */
    public void appendEntryIndex(int index, long offset, int kind, int term) throws IOException {
        if (seekableFile.size() == 0L) {
            seekableFile.writeInt(index);
            minEntryIndex = index;
        } else {
            if (index != maxEntryIndex + 1) {
                throw new IllegalArgumentException("index should be " + (maxEntryIndex + 1) + ", bus was " + index);
            }
            // skip min entry index
            seekableFile.seek(OFFSET_ENTRY_INDEX);
        }

        // write max entry index
        seekableFile.writeInt(index);
        maxEntryIndex = index;
        updateEntryIndexCount();

        // move to position after last entry offset
        seekableFile.seek(getOffsetEntryIndexItem(index));
        seekableFile.writeLong(offset);
        seekableFile.writeInt(kind);
        seekableFile.writeInt(term);

        entryIndexMap.put(index, new EntryIndexItem(index, offset, kind, term));
    }

    /**
     * Remove log entries index whose index is greater than the specified index.
     *
     * @param newMaxEntryIndex new max log entry index
     * @throws IOException if IO exception occurs
     */
    public void removeAfter(int newMaxEntryIndex) throws IOException {
        if (isEmpty() || newMaxEntryIndex >= maxEntryIndex) {
            return;
        }
        if (newMaxEntryIndex < minEntryIndex) {
            clear();
            return;
        }
        // move to position after min log entry index
        seekableFile.seek(OFFSET_ENTRY_INDEX);
        // rewrite max log entry index
        seekableFile.writeInt(newMaxEntryIndex);
        // reset size of the index file
        seekableFile.truncate(getOffsetEntryIndexItem(newMaxEntryIndex + 1));
        // remove the log entry index in map
        for (int i = newMaxEntryIndex + 1; i <= maxEntryIndex; i++) {
            entryIndexMap.remove(i);
        }
        maxEntryIndex = newMaxEntryIndex;
        entryIndexCount = newMaxEntryIndex - minEntryIndex + 1;
    }

    /**
     * Clear the index file.
     *
     * @throws IOException if IO exception occurs
     */
    public void clear() throws IOException {
        seekableFile.truncate(0L);
        entryIndexCount = 0;
        entryIndexMap.clear();
    }

    /**
     * Get the offset of the log entry whose index equals to the specified index.
     *
     * @param entryIndex the specified index
     * @return the offset of the log entry
     */
    public long getOffset(int entryIndex) {
        return get(entryIndex).getOffset();
    }

    /**
     * Get the entry index item whose index equals to the specified index.
     *
     * @param entryIndex the specified index
     * @throws IllegalArgumentException if {@code entryIndex < minEntryIndex}
     *                                  or {@code entryIndex > maxEntryIndex}
     * @return the entry index item
     */
    @Nonnull
    public EntryIndexItem get(int entryIndex) {
        checkEmpty();
        if (entryIndex < minEntryIndex || entryIndex > maxEntryIndex) {
            throw new IllegalArgumentException("index < min or index > max");
        }
        return entryIndexMap.get(entryIndex);
    }

    private void load() throws IOException {
        if (seekableFile.size() == 0L) {
            entryIndexCount = 0;
            return;
        }
        minEntryIndex = seekableFile.readInt();
        maxEntryIndex = seekableFile.readInt();
        updateEntryIndexCount();
        long offset;
        int kind;
        int term;
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            offset = seekableFile.readLong();
            kind = seekableFile.readInt();
            term = seekableFile.readInt();
            entryIndexMap.put(i, new EntryIndexItem(i, offset, kind, term));
        }
    }

    private void updateEntryIndexCount() {
        entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }

    private void checkEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("no entry index");
        }
    }

    private long getOffsetEntryIndexItem(int index) {
        return (OFFSET_ENTRY_INDEX << 1) + (long) (index - minEntryIndex) * LENGTH_ENTRY_INDEX_ITEM;
    }

    @Override
    @Nonnull
    public Iterator<EntryIndexItem> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new EntryIndexIterator(entryIndexCount, minEntryIndex);
    }

    private class EntryIndexIterator implements Iterator<EntryIndexItem> {

        private final int entryIndexCount;
        private int currentEntryIndex;

        EntryIndexIterator(int entryIndexCount, int minEntryIndex) {
            this.entryIndexCount = entryIndexCount;
            currentEntryIndex = minEntryIndex;
        }

        private void checkModification() {
            if (this.entryIndexCount != EntryIndexFile.this.entryIndexCount) {
                throw new IllegalStateException("entry index count changed");
            }
        }

        @Override
        public boolean hasNext() {
            checkModification();
            return currentEntryIndex <= maxEntryIndex;
        }

        @Override
        public EntryIndexItem next() {
            checkModification();
            return entryIndexMap.get(currentEntryIndex++);
        }

    }

}
