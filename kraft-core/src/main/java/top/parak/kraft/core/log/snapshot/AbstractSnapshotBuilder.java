package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogException;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import java.io.IOException;
import java.util.Set;

/**
 * Abstract snapshot builder.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public abstract class AbstractSnapshotBuilder<T extends Snapshot> implements SnapshotBuilder<T> {

    /**
     * The index of the last log entry in the snapshot.
     */
    int lastIncludedIndex;
    /**
     * The term of the last log entry in the snapshot.
     */
    int lastIncludedTerm;
    /**
     * The last group config in the snapshot.
     */
    Set<NodeEndpoint> lastConfig;
    /**
     * The file-pointer of the snapshot.
     */
    private int offset;

    /**
     * Create AbstractSnapshotBuilder.
     *
     * @param firstRpc the first installSnapshotRpc
     */
    AbstractSnapshotBuilder(InstallSnapshotRpc firstRpc) {
        assert firstRpc.getOffset() == 0;
        lastIncludedIndex = firstRpc.getLastIndex();
        lastIncludedTerm = firstRpc.getLastTerm();
        lastConfig = firstRpc.getLastConfig();
        offset = firstRpc.getDataLength();
    }

    /**
     * Write data.
     *
     * @param data data.
     */
    protected void write(byte[] data) {
        try {
            doWrite(data);
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    /**
     * Do write data.
     *
     * @param data data
     * @throws IOException if IO exception occurs
     */
    protected abstract void doWrite(byte[] data) throws IOException;

    @Override
    public void append(InstallSnapshotRpc rpc) {
        if (rpc.getOffset() != offset) {
            throw new IllegalArgumentException("unexpected offset, expected " + offset + ", but was " + rpc.getOffset());
        }
        if (rpc.getLastIndex() != lastIncludedIndex || rpc.getLastTerm() != lastIncludedTerm) {
            throw new IllegalArgumentException("unexpected last included index or term");
        }
        write(rpc.getData());
        offset += rpc.getDataLength();
    }

}
