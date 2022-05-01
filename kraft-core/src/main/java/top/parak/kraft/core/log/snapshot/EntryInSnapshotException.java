package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogException;

/**
 * Thrown when {@code nextIndex} <= {@code lastIncludedIndex}.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class EntryInSnapshotException extends LogException {

    /**
     * See {@code nextIndex}.
     */
    private final int index;

    /**
     * Create EntryInSnapshotException.
     *
     * @param index nextIndex
     */
    public EntryInSnapshotException(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
