package top.parak.kraft.core.node.store;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.support.file.Files;
import top.parak.kraft.core.support.file.RandomAccessFileAdapter;
import top.parak.kraft.core.support.file.SeekableFile;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;

/**
 * Store node status via file.
 *
 * <p><b>Structure of node file</b></p>
 * {@link FileNodeStore} use {@link SeekableFile} to store node's status, included
 * the current term of node and the voted for of node. The file will use 4 bytes to
 * record the current term of node and 4 bytes to record the length of the voted for
 * of node, then use unfixed bytes to record the content of the voted for of node.
 * <pre>
 * +---------------------+---------------------+---------------------+
 * |        int(4)       |        int(4)       |        bytes        |
 * +---------------------+---------------------+---------------------+
 * |     current term    |  length of votedFor |       votedFor      |
 * +---------------------+---------------------+---------------------+
 * </pre>
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class FileNodeStore implements NodeStore {

    /**
     * The name of file used to store node's status.
     */
    public static final String FILE_NAME = "node.bin";
    /**
     * The offset of file to record the term of node whose type is {@code int}.
     * Since the term of node is recorded in the beginning of the file, this constant value is 0.
     */
    private static final long OFFSET_TERM = 0;
    /**
     * The offset of file to record the length of voted for of node whose type is {@code int}.
     * Since the term of node takes up 4 bytes, this constant value is 4.
     */
    private static final long OFFSET_VOTED_FOR = 4;

    /**
     * The file used to store node status.
     */
    private final SeekableFile seekableFile;
    /**
     * The currentTerm of node stored in {@link #seekableFile}.
     */
    private int term = 0;
    /**
     * The votedFor of node stored in {@link #seekableFile}.
     */
    private NodeId votedFor = null;

    /**
     * Create FileNodeStore.
     *
     * @param file file
     */
    public FileNodeStore(File file) {
        try {
            if (!file.exists()) {
                Files.touch(file);
            }
            seekableFile = new RandomAccessFileAdapter(file);
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    /**
     * Create FileNodeStore.
     *
     * @param seekableFile seekableFile
     */
    public FileNodeStore(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
        try {
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    private void initializeOrLoad() throws IOException {
        if (seekableFile.size() == 0) {
            // (term, 4) + (votedFor length, 4) = 8
            seekableFile.truncate(8L);
            seekableFile.seek(0);
            seekableFile.writeInt(0); // term
            seekableFile.writeInt(0); // votedFor length
        } else {
            // read term
            term = seekableFile.readInt();
            // read voted for
            int length = seekableFile.readInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                seekableFile.read(bytes);
                votedFor = new NodeId(new String(bytes));
            }
        }
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        try {
            seekableFile.seek(OFFSET_TERM);
            seekableFile.writeInt(term);
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.term = term;
    }

    @Override
    public NodeId getVotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(NodeId votedFor) {
        try {
            seekableFile.seek(OFFSET_VOTED_FOR);
            if (votedFor == null) {
                seekableFile.writeInt(0);
            } else {
                byte[] bytes = votedFor.getValue().getBytes();
                seekableFile.writeInt(bytes.length);
                seekableFile.write(bytes);
            }
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.votedFor = votedFor;
    }

    @Override
    public void close() {
        try {
            seekableFile.close();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

}
