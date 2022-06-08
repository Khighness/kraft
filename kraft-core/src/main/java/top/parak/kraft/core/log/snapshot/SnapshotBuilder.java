package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

public interface SnapshotBuilder<T extends Snapshot> {

    void append(InstallSnapshotRpc rpc);

    T build();

    void close();

}
