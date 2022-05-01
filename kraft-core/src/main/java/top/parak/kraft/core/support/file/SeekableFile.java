package top.parak.kraft.core.support.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * Seekable file.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
public interface SeekableFile {

    /**
     * Return the file-pointer offset.
     *
     * @return position
     * @throws IOException if IO exception occurs
     */
    long position() throws IOException;

    /**
     * Set the file-pointer offset.
     *
     * @throws IOException if IO exception occurs
     */
    void seek(long position) throws IOException;

    /**
     * Write integer to file.
     *
     * @param i the integer to be written
     * @throws IOException if IO exception occurs
     */
    void writeInt(int i) throws IOException;

    /**
     * Write long to file.
     *
     * @param l the long to be written
     * @throws IOException if IO exception occurs
     */
    void writeLong(long l) throws IOException;

    /**
     * Write bytes to file.
     *
     * @param b the bytes to be written
     * @throws IOException if IO exception occurs
     */
    void write(byte[] b) throws IOException;

    /**
     * Read integer from file.
     *
     * @throws IOException if IO exception occurs
     */
    int readInt() throws IOException;

    /**
     * Read long from file.
     *
     * @throws IOException if IO exception occurs
     */
    long readLong() throws IOException;

    /**
     * Read bytes from file to {@code b}.
     *
     * @return read length
     * @throws IOException if IO exception occurs
     */
    int read(byte[] b) throws IOException;

    /**
     * Return the length of file.
     *
     * @return the length of file
     * @throws IOException if IO exception occurs
     */
    long size() throws IOException;

    /**
     * Truncate file to the specified size.
     *
     * @param size the expected size
     * @throws IOException if IO exception occurs
     */
    void truncate(long size) throws IOException;

    /**
     * Return the input stream of file staring at the specified position.
     *
     * @param start the specified position
     * @return the input stream of file staring at the specified position
     * @throws IOException if IO exception occurs
     */
    InputStream inputStream(long start) throws IOException;

    /**
     * Force file output to disk.
     *
     * @throws IOException if IO exception occurs
     */
    void flush() throws IOException;

    /**
     * Close the file.
     *
     * @throws IOException if IO exception occurs
     */
    void close() throws IOException;

}
