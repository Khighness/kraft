package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.Entry;
import top.parak.kraft.core.log.entry.EntryFactory;
import top.parak.kraft.core.support.file.RandomAccessFileAdapter;
import top.parak.kraft.core.support.file.SeekableFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Log entries file.
 *
 * <p><b>Structure of log entries file</b></p>
 * {@link EntriesFile} organizes file in lines, and each lines records the log entry kind (4 byte),
 * the log entry index (4 byte), the log entry term (4 byte), the length of command (4 byte) and
 * the specific command content (unfixed byte).
 * <pre>
 * +----------+----------+----------+----------+----------------+
 * |   int(4) |   int(4) |   int(4) |   int(4) |     bytes      |
 * +----------+----------+----------+----------+----------------+
 * |   kind   |   index  |   term   |  length  |  command bytes |
 * +----------+----------+----------+----------+----------------+
 * |   kind   |   index  |   term   |  length  |  command bytes |
 * +----------+----------+----------+----------+----------------+
 * </pre>
 * {@link EntriesFile} has no structure like a file header, and fast access relies on {@link
 * EntryIndexFile}.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
public class EntriesFile {

    private final SeekableFile seekableFile;

    /**
     * Create EntriesFile.
     *
     * @param file file
     * @throws FileNotFoundException if file can not be found
     */
    public EntriesFile(File file) throws FileNotFoundException {
        this(new RandomAccessFileAdapter(file));
    }

    /**
     * Create EntriesFile.
     *
     * @param seekableFile seekableFile
     */
    public EntriesFile(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
    }

    /**
     * Append entry to EntriesFile.
     *
     * @param entry entry
     * @return offset
     * @throws IOException if IO exception occurs
     */
    public long appendEntry(Entry entry) throws IOException {
        long offset = seekableFile.size();
        seekableFile.seek(offset);
        seekableFile.writeInt(entry.getKind());
        seekableFile.writeInt(entry.getIndex());
        seekableFile.writeInt(entry.getTerm());
        byte[] commandBytes = entry.getCommandBytes();
        seekableFile.writeInt(commandBytes.length);
        seekableFile.write(commandBytes);
        return offset;
    }

    /**
     * Load entry from EntriesFile.
     *
     * @param offset  offset
     * @param factory factory
     * @return entry
     * @throws IOException if IO exception occurs
     */
    public Entry loadEntry(long offset, EntryFactory factory) throws IOException {
        if (offset > seekableFile.size()) {
            throw new IllegalArgumentException("offset > size");
        }
        seekableFile.seek(offset);
        int kind = seekableFile.readInt();
        int index = seekableFile.readInt();
        int term = seekableFile.readInt();
        int length = seekableFile.readInt();
        byte[] commandBytes = new byte[length];
        seekableFile.read(commandBytes);
        return factory.create(kind, index, term, commandBytes);
    }

    /**
     * Return the size of EntriesFile.
     *
     * @return the size of EntriesFile
     * @throws IOException if IO exception occurs
     */
    public long size() throws IOException {
        return seekableFile.size();
    }

    /**
     * Clear EntriesFile.
     *
     * @throws IOException if IO exception occurs
     */
    public void clear() throws IOException {
        truncate(0L);
    }

    /**
     * Truncate EntriesFile.
     *
     * @param offset offset
     * @throws IOException if IO exception occurs
     */
    public void truncate(long offset) throws IOException {
        seekableFile.truncate(offset);
    }

    /**
     * Close EntriesFile.
     *
     * @throws IOException if IO exception occurs
     */
    public void close() throws IOException {
        seekableFile.close();
    }

}
