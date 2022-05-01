package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.List;

/**
 * Abstract entry sequence.
 *
 * <p><b>Structure of entry sequence</b></p>
 * <pre>
 * +-----------+-----------+-----------+-----------+- - - - - -+
 * |   entry   |   entry   |   entry   |   entry   |   entry   |
 * +-----------+-----------+-----------+-----------+- - - - - -+
 *       ↑                                   ↑           ↑
 * logIndexOffset(firstLogIndex)        lastLogIndex nextLogIndex
 * </pre>
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
@NotThreadSafe
abstract class AbstractEntrySequence implements EntrySequence {

    /**
     * The index of the first log entry stored in the log entry sequence
     * while also called {@code firstLogIndex}, initialize as 1.
     */
    int logIndexOffset;

    /**
     * The index of the next log entry to store in the log entry sequence
     * which is equal to {@code lastLogIndex - 1}, initialize as 1.
     */
    int nextLogIndex;

    /**
     * Create AbstractEntrySequence.
     *
     * @param logIndexOffset the index of the first log entry in file
     */
    public AbstractEntrySequence(int logIndexOffset) {
        this.logIndexOffset = logIndexOffset;
        this.nextLogIndex = logIndexOffset;
    }

    @Override
    public boolean isEmpty() {
        return logIndexOffset == nextLogIndex;
    }

    @Override
    public int getFirstLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        return doGetFirstLogIndex();
    }

    @Override
    public int getLastLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        return doGetLastLogIndex();
    }

    @Override
    public int getNextLogIndex() {
        return nextLogIndex;
    }

    @Override
    public List<Entry> subView(int fromIndex) {
        if (isEmpty() || fromIndex > doGetLastLogIndex()) {
            return Collections.emptyList();
        }
        return subList(Math.max(fromIndex, doGetFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        if (fromIndex < doGetFirstLogIndex() || toIndex > doGetLastLogIndex() + 1 || fromIndex > toIndex) {
            throw new IllegalArgumentException("illegal from index " + fromIndex + "or to index " + toIndex);
        }
        return doSubList(fromIndex, toIndex);
    }

    @Override
    public boolean isEntryPresent(int index) {
        return !isEmpty() && index >= doGetFirstLogIndex() && index <= doGetLastLogIndex();
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        Entry entry = getEntry(index);
        return entry != null ? entry.getMeta() : null;
    }

    @Override
    public Entry getEntry(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        return doGetEntry(index);
    }

    @Override
    public Entry getLastEntry() {
        return isEmpty() ? null : doGetEntry(doGetLastLogIndex());
    }

    @Override
    public void append(Entry entry) {
        if (entry.getIndex() != nextLogIndex) {
            throw new IllegalArgumentException("entry index must be " + nextLogIndex);
        }
        doAppend(entry);
        nextLogIndex++;
    }

    @Override
    public void append(List<Entry> entries) {
        entries.forEach(this::append);
    }

    @Override
    public void removeAfter(int index) {
        if (isEmpty() || index >= doGetLastLogIndex()) {
            return;
        }
        doRemoveAfter(index);
    }

    /**
     * Do get the log entry whose index equals to the specified index.
     *
     * @param index the specified index
     * @return the log entry
     */
    protected abstract Entry doGetEntry(int index);

    /**
     * Do get the index of the first log entry stored in the log entry sequence.
     *
     * @return the first index of the log entry
     */
    int doGetFirstLogIndex() {
        return logIndexOffset;
    }

    /**
     * Do get the index of last the log entry stored in the log entry sequence.
     *
     * @return the last index of the log entry
     */
    int doGetLastLogIndex() {
        return nextLogIndex - 1;
    }

    /**
     * Do get the log entries whose index is greater than (can equal) {@code fromIndex} and
     * less than (can not equal) {@code toIndex}.
     *
     * @param fromIndex from index
     * @param toIndex   to index
     * @return sub list [fromIndex, toIndex)
     */
    protected abstract List<Entry> doSubList(int fromIndex, int toIndex);

    /**
     * Do append a log entry to cache.
     *
     * @param entry the log entry
     */
    protected abstract void doAppend(Entry entry);

    /**
     * Do remove the log entry whose index is greater than the specified index.
     *
     * @param index the specified index
     */
    protected abstract void doRemoveAfter(int index);

}
