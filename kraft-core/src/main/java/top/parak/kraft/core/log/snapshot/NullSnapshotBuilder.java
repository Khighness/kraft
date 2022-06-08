package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

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
