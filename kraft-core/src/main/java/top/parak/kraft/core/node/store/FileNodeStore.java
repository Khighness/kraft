package top.parak.kraft.core.node.store;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.support.file.Files;
import top.parak.kraft.core.support.file.RandomAccessFileAdapter;
import top.parak.kraft.core.support.file.SeekableFile;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * File node store.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public class FileNodeStore implements NodeStore {
//
//    public static final String FILE_NAME = "node.bin";
//    private static final long OFFSET_TERM = 0;
//    private static final long OFFSET_VOTED_FOR = 4;
//    private final SeekableFile seekableFile;
//    private int term = 0;
//    private NodeId votedFor = null;
//
//    public FileNodeStore(File file) {
//        try {
//            if (!file.exists()) {
//                Files.touch(file);
//            }
//            seekableFile = new RandomAccessFileAdapter(file);
//        } catch (IOException e) {
//            throw new NodeStoreException(e);
//        }
//    }
//
//    public FileNodeStore(SeekableFile seekableFile) {
//        this.seekableFile = seekableFile;
//        try {
//
//        } catch (IOException e) {
//            throw new NodeStoreException(e);
//        }
//    }
//
//    private void initializeOrLoad() throws IOException {
//        if (seekableFile.size() == 0) {
//            // (term, 4) + (votedFor length, 4) = 8
//        } else {
//
//        }
//    }

    @Override
    public int getTerm() {
        return 0;
    }

    @Override
    public void setTerm(int term) {

    }

    @Nullable
    @Override
    public NodeId getNotedFor() {
        return null;
    }

    @Override
    public void setVotedFor(@Nullable NodeId nodeId) {

    }

    @Override
    public void close() {

    }

}
