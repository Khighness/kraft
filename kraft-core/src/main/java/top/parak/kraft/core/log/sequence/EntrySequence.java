package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryMeta;

import java.util.List;

/**
 * Log entry sequence.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public interface EntrySequence {

    /**
     * Return if the log entry sequence is empty.
     *
     * @return true if the log entry sequence is empty, otherwise false.
     */
    boolean isEmpty();

    /**
     * Get the index of the first log entry stored in the log entry sequence.
     *
     * @return the first index of the log entry.
     */
    int getFirstLogIndex();

    /**
     * Get the index of the last log entry stored in the log entry sequence.
     *
     * @return the last index of the log entry.
     */
    int getLastLogIndex();

    /**
     * Get the index of the next log entry to be stored in the log entry sequence.
     *
     * @return the next index of the log entry.
     */
    int getNextLogIndex();

    /**
     * Return the log entries whose index is between {@code fromIndex} and the last index.
     *
     * @param fromIndex from index
     * @return sub list [fromIndex, lastIndex]
     */
    List<Entry> subView(int fromIndex);

    /**
     * Returns the log entries whose index is greater than (can equal) {@code fromIndex} and
     * less than (can not equal) {@code toIndex}.
     *
     * @param fromIndex from index
     * @param toIndex   to index
     * @return sub list [fromIndex, toIndex)
     */
    List<Entry> subList(int fromIndex, int toIndex);

    /**
     * Check if the log entry whose index equals to the specified index exists.
     *
     * @param index the index of the log entry
     * @return true if the log entry exists, otherwise false
     */
    boolean isEntryPresent(int index);

    /**
     * Get the metadata of the log entry whose index equals to the specified index.
     *
     * @param index the index of the log entry
     * @return the medata of the log entry
     */
    EntryMeta getEntryMeta(int index);

    /**
     * Get the log entry whose index equals to the specified index.
     *
     * @param index the index of the log entry
     * @return the log entry
     */
    Entry getEntry(int index);

    /**
     * Get the last log entry of the log entry sequence.
     *
     * @return the last log entry
     */
    Entry getLastEntry();

    /**
     * Append the log entry to the log entry sequence.
     *
     * @param entry the log entry
     */
    void append(Entry entry);

    /**
     * Append the log entries to the log entry sequence.
     *
     * @param entries the log entries
     */
    void append(List<Entry> entries);

    /**
     * Advance {@code commitIndex}, just write the log entries in cache
     * to the log entry file and the log entry index file.
     *
     * @param index {@code commitIndex}
     */
    void commit(int index);

    /**
     * Get {@code commitIndex} of the log entry sequence.
     *
     * @return {@code commitIndex}
     */
    int getCommitIndex();

    /**
     * Remove the log entry whose index is greater than the specified index.
     *
     * @param index the specified index
     */
    void removeAfter(int index);

    /**
     * Close the log entry sequence.
     */
    void close();

    /**
     * Build a list of group config entries in the log entry sequence.
     *
     * @return  the group config entry list.
     */
    GroupConfigEntryList buildGroupConfigEntryList();

}
