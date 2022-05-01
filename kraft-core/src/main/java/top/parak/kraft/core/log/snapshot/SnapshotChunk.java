package top.parak.kraft.core.log.snapshot;

/**
 * Snapshot chunk.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public class SnapshotChunk {

    /**
     * The bytes of snapshot chunk.
     */
    private final byte[] bytes;
    /**
     * If this chunk is last.
     */
    private final boolean lastChunk;

    /**
     * Create SnapshotChunk.
     *
     * @param bytes     the bytes of snapshot chunk
     * @param lastChunk if this chunk is last.
     */
    SnapshotChunk(byte[] bytes, boolean lastChunk) {
        this.bytes = bytes;
        this.lastChunk = lastChunk;
    }

    /**
     * Check if this chunk is last.
     *
     * @return if this chunk is last
     */
    public boolean isLastChunk() {
        return lastChunk;
    }

    /**
     * Return the bytes of snapshot bytes.
     *
     * @return the bytes of snapshot bytes
     */
    public byte[] toByteArray() {
        return bytes;
    }

}
