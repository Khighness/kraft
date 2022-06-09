package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.node.NodeEndpoint;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Memory-based snapshot.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
@Immutable
public class MemorySnapshot implements Snapshot {

    /**
     * The index of the last log entry in the snapshot.
     */
    private final int lastIncludedIndex;
    /**
     * The term of the last log entry in the snapshot.
     */
    private final int lastIncludedTerm;
    /**
     * The byte array to store snapshot.
     */
    private final byte[] data;
    /**
     * The last group config in the snapshot.
     */
    private final Set<NodeEndpoint> lastConfig;


    /**
     * Create MemorySnapShot.
     *
     * @param lastIncludedIndex last index
     * @param lastIncludedTerm  last term
     */
    public MemorySnapshot(int lastIncludedIndex, int lastIncludedTerm) {
        this(lastIncludedIndex, lastIncludedTerm, new byte[0], Collections.emptySet());
    }

    /**
     * Create MemorySnapshot.
     *
     * @param lastIncludedIndex last index
     * @param lastIncludedTerm  last term
     * @param data              byte array
     * @param lastConfig         last config
     */
    public MemorySnapshot(int lastIncludedIndex, int lastIncludedTerm, byte[] data, Set<NodeEndpoint> lastConfig) {
        this.lastIncludedIndex = lastIncludedIndex;
        this.lastIncludedTerm = lastIncludedTerm;
        this.data = data;
        this.lastConfig = lastConfig;
    }

    @Override
    public int getLastIncludedIndex() {
        return lastIncludedIndex;
    }

    @Override
    public int getLastIncludedTerm() {
        return lastIncludedTerm;
    }

    @Nonnull
    @Override
    public Set<NodeEndpoint> getLastConfig() {
        return lastConfig;
    }

    @Override
    public long getDataSize() {
        return data.length;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    @Nonnull
    public SnapshotChunk readData(int offset, int length) {
        if (offset < 0 || offset > data.length) {
            throw new IndexOutOfBoundsException("offset " + offset + " out of bound");
        }

        int bufferLength = Math.min(data.length - offset, length);
        byte[] buffer = new byte[bufferLength];
        System.arraycopy(data, offset, buffer, 0, bufferLength);
        return new SnapshotChunk(buffer, offset + length >= this.data.length);
    }

    @Override
    @Nonnull
    public InputStream getDataStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return "MemorySnapshot{" +
                "lastIncludedIndex=" + lastIncludedIndex +
                ", lastIncludedTerm=" + lastIncludedTerm +
                ", data.size=" + data.length +
                '}';
    }

}
