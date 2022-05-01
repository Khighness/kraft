package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.log.LogDir;
import top.parak.kraft.core.log.LogException;
import top.parak.kraft.core.rpc.message.InstallSnapshotRpc;

import java.io.IOException;

/**
 * File-based snapshot builder.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class FileSnapshotBuilder extends AbstractSnapshotBuilder<FileSnapshot> {

    /**
     * The log directory.
     */
    private final LogDir logDir;
    /**
     * The writer of snapshot file.
     */
    private FileSnapshotWriter writer;

    /**
     * Create FileSnapshotBuilder.
     *
     * @param firstRpc the first installSnapshotRpc
     * @param logDir  the log directory
     */
    public FileSnapshotBuilder(InstallSnapshotRpc firstRpc, LogDir logDir) {
        super(firstRpc);
        this.logDir = logDir;

        try {
            writer = new FileSnapshotWriter(logDir.getSnapshotFile(), firstRpc.getLastIndex(), firstRpc.getLastTerm(), firstRpc.getLastConfig());
            writer.write(firstRpc.getData());
        } catch (IOException e) {
            throw new LogException("failed to write snapshot data to file", e);
        }
    }

    @Override
    protected void doWrite(byte[] data) throws IOException {
        writer.write(data);
    }

    @Override
    public FileSnapshot build() {
        close();
        return new FileSnapshot(logDir);
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new LogException("failed to close writer", e);
        }
    }

}
