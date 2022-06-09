package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogException;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Memory-based snapshot builder.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class MemorySnapshotBuilder extends AbstractSnapshotBuilder<MemorySnapshot> {

    /**
     * The writer of the snapshot.
     */
    private final ByteArrayOutputStream output;

    /**
     * Create MemorySnapshotBuilder.
     *
     * @param firstRpc the first installSnapshotRpc
     */
    public MemorySnapshotBuilder(InstallSnapshotRpc firstRpc) {
        super(firstRpc);
        output = new ByteArrayOutputStream();

        try {
            output.write(firstRpc.getData());
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    @Override
    protected void doWrite(byte[] data) throws IOException {
        output.write(data);
    }

    @Override
    public MemorySnapshot build() {
        return new MemorySnapshot(lastIncludedIndex, lastIncludedTerm, output.toByteArray(), lastConfig);
    }

    @Override
    public void close() {
    }

}
