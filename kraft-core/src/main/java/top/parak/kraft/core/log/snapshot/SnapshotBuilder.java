package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

/**
 * Snapshot builder.
 * <p>Handle different types of log snapshots via {@code T}.</p>
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public interface SnapshotBuilder<T extends Snapshot> {

    /**
     * Append snapshot rpc.
     *
     * @param rpc rpc
     */
    void append(InstallSnapshotRpc rpc);

    /**
     * Build snapshot.
     *
     * @return T custom type
     */
    T build();

    /**
     * Close snapshot.
     */
    void close();

}
