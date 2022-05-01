package top.parak.kraft.core.log.snapshot;

import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.support.proto.Protos;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Writer of snapshot file.
 *
 * @author KHighness
 * @since 2022-04-06
 * @email parakovo@gmail.com
 */
public class FileSnapshotWriter implements AutoCloseable {

    /**
     * The output stream.
     */
    private final DataOutputStream output;

    /**
     * Create FileSnapshotWriter.
     *
     * @param file               output file
     * @param lastIncludedIndex last index
     * @param lastIncludedTerm  last term
     * @param lastConfig         last config
     * @throws IOException if IO exception occurs
     */
    public FileSnapshotWriter(File file, int lastIncludedIndex, int lastIncludedTerm, Set<NodeEndpoint> lastConfig) throws IOException {
        this(new DataOutputStream(new FileOutputStream(file)), lastIncludedIndex, lastIncludedTerm, lastConfig);
    }

    /**
     * Create FileSnapshotWriter.
     *
     * @param output            output stream
     * @param lastIncludedIndex last index
     * @param lastIncludedTerm  last term
     * @param lastConfig         last config
     * @throws IOException if IO exception occurs
     */
    FileSnapshotWriter(OutputStream output, int lastIncludedIndex, int lastIncludedTerm, Set<NodeEndpoint> lastConfig) throws IOException {
        this.output = new DataOutputStream(output);
        byte[] headerBytes = Protos.SnapshotHeader.newBuilder()
                .setLastIndex(lastIncludedIndex)
                .setLastTerm(lastIncludedTerm)
                .addAllLastConfig(
                        lastConfig.stream()
                                .map(e -> Protos.NodeEndpoint.newBuilder()
                                        .setId(e.getId().getValue())
                                        .setHost(e.getHost())
                                        .setPort(e.getPort())
                                        .build())
                                .collect(Collectors.toList()))
                .build().toByteArray();
        this.output.writeInt(headerBytes.length);
        this.output.write(headerBytes);
    }

    /**
     * Get the output stream.
     *
     * @return the output stream
     */
    public OutputStream getOutput() {
        return output;
    }

    /**
     * Write data.
     *
     * @param data data
     * @throws IOException if occur o exception
     */
    public void write(byte[] data) throws IOException {
        output.write(data);
    }

    @Override
    public void close() throws IOException {
        output.close();
    }

}
