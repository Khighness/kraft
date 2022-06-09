package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogException;

/**
 * Thrown when creating {@link top.parak.kraft.core.rpc.message.AppendEntriesRpc},
 * the index of log entry to be replicated is included in the snapshot. Then leader
 * will try to send {@link top.parak.kraft.core.rpc.message.InstallSnapshotRpc}.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class EntryInSnapshotException extends LogException {

    /**
     * The index of log entry which is included in snapshot.
     */
    private final int index;

    /**
     * Create EntryInSnapshotException.
     *
     * @param index the index of log entry
     */
    public EntryInSnapshotException(int index) {
        this.index = index;
    }

    /**
     * Get the index of log entry which is included in snapshot.
     *
     * @return the index of log entry
     */
    public int getIndex() {
        return index;
    }

}
