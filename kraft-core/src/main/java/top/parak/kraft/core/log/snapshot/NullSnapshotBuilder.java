package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

/**
 * Null snapshot builder/
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public class NullSnapshotBuilder implements SnapshotBuilder {

    @Override
    public void append(InstallSnapshotRpc rpc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Snapshot build() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

}
