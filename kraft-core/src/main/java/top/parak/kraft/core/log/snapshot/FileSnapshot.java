package top.parak.kraft.core.log.snapshot;

import com.google.protobuf.InvalidProtocolBufferException;

import top.parak.kraft.core.log.LogDir;
import top.parak.kraft.core.log.LogException;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.support.file.RandomAccessFileAdapter;
import top.parak.kraft.core.support.file.SeekableFile;
import top.parak.kraft.core.support.proto.Protos;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File-based snapshot.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
@Immutable
public class FileSnapshot implements Snapshot {

    /**
     * The log directory.
     */
    private LogDir logDir;
    /**
     * The file to store snapshot.
     */
    private SeekableFile seekableFile;
    /**
     * The index of the last log entry in the snapshot.
     */
    private int lastIncludedIndex;
    /**
     * The term of the last log entry in the snapshot.
     */
    private int lastIncludedTerm;
    /**
     * The last group config.
     */
    private Set<NodeEndpoint> lastConfig;
    /**
     * The offset of the data starting position.
     */
    private long dataStart;
    /**
     * The length of the data.
     */
    private long dataLength;

    /**
     * Create FileSnapshot.
     *
     * @param logDir the log dir
     */
    public FileSnapshot(LogDir logDir) {
        this.logDir = logDir;
        readHeader(logDir.getSnapshotFile());
    }

    /**
     * Create FileSnapshot.
     *
     * @param file file
     */
    public FileSnapshot(File file) {
        readHeader(file);
    }

    /**
     * Create FileSnapshot.
     *
     * @param seekableFile seekableFile
     */
    public FileSnapshot(SeekableFile seekableFile) {
        readHeader(seekableFile);
    }

    private void readHeader(File file) {
        try {
            readHeader(new RandomAccessFileAdapter(file, "r"));
        } catch (FileNotFoundException e) {
            throw new LogException(e);
        }
    }

    private void readHeader(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
        try {
            int headerLength = seekableFile.readInt();
            byte[] headerBytes = new byte[headerLength];
            seekableFile.read(headerBytes);
            Protos.SnapshotHeader header = Protos.SnapshotHeader.parseFrom(headerBytes);
            lastIncludedIndex = header.getLastIndex();
            lastIncludedTerm = header.getLastTerm();
            // read group config
            lastConfig = header.getLastConfigList().stream()
                    .map(e -> new NodeEndpoint(e.getId(), e.getHost(), e.getPort()))
                    .collect(Collectors.toSet());
            dataStart = seekableFile.position();
            dataLength = seekableFile.size() - dataStart;
        } catch (InvalidProtocolBufferException e) {
            throw new LogException("failed to parse header of snapshot", e);
        } catch (IOException e) {
            throw new LogException("failed to read snapshot", e);
        }
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
        return dataLength;
    }

    @Override
    public SnapshotChunk readData(int offset, int length) {
        if (offset > dataLength) {
            throw new IllegalArgumentException("offset > data length");
        }
        try {
            seekableFile.seek(dataStart + offset);
            byte[] buffer = new byte[Math.min(length, (int) dataLength - offset)];
            int n = seekableFile.read(buffer);
            return new SnapshotChunk(buffer, offset + n >= dataLength);
        } catch (IOException e) {
            throw new LogException("failed to seek or read snapshot content", e);
        }
    }

    @Nonnull
    @Override
    public InputStream getDataStream() {
        try {
            return seekableFile.inputStream(dataStart);
        } catch (IOException e) {
            throw new LogException("failed to get input stream of snapshot data", e);
        }
    }

    @Override
    public void close() {
        try {
            seekableFile.close();
        } catch (IOException e) {
            throw new LogException("failed to close file", e);
        }
    }

    public LogDir getLogDir() {
        return logDir;
    }

}
