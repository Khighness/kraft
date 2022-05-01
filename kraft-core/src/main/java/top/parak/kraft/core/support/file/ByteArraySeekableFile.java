package top.parak.kraft.core.support.file;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * {@link SeekableFile} implementation based on byte array.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
public class ByteArraySeekableFile implements SeekableFile {

    /**
     * The container to store bytes.
     */
    private byte[] content;
    /**
     * The size of file.
     */
    private int size;
    /**
     * The file-pointer.
     */
    private int position;

    /**
     * Create ByteArraySeekableFile.
     */
    public ByteArraySeekableFile() {
        this(new byte[0]);
    }

    /**
     * Create ByteArraySeekableFile.
     *
     * @param content content
     */
    public ByteArraySeekableFile(byte[] content) {
        this.content = content;
        this.size = content.length;
        this.position = 0;
    }

    /**
     * Check position.
     *
     * @param position position
     * @throws IllegalArgumentException if position is less than 0 or position is greater than size
     */
    private void checkPosition(long position) {
        if (position < 0 || position > size) {
            throw new IllegalArgumentException("offset < 0 or offset > size");
        }
    }

    /**
     * Ensure capacity.
     *
     * @param capacity capacity
     */
    private void ensureCapacity(int capacity) {
        int oldLength = content.length;
        if (position + capacity <= oldLength) {
            return;
        }
        if (oldLength == 0) {
            content = new byte[capacity];
            return;
        }
        int newLength = (oldLength >= capacity ? oldLength << 1 : oldLength + capacity);
        byte[] newContent = new byte[newLength];
        System.arraycopy(content, 0, newContent, 0, oldLength);
        content = newContent;
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public void seek(long position) throws IOException {
        checkPosition(position);
        this.position = (int) position;
    }

    @Override
    public void writeInt(int i) throws IOException {
        write(Ints.toByteArray(i));
    }

    @Override
    public void writeLong(long l) throws IOException {
        write(Longs.toByteArray(l));
    }

    @Override
    public void write(byte[] b) throws IOException {
        int n = b.length;
        ensureCapacity(n);
        System.arraycopy(b, 0, content, position, n);
        size = Math.max(position + n, size);
        position += n;
    }

    @Override
    public int readInt() throws IOException {
        byte[] buffer = new byte[4];
        read(buffer);
        return Ints.fromByteArray(buffer);
    }

    @Override
    public long readLong() throws IOException {
        byte[] buffer = new byte[8];
        read(buffer);
        return Longs.fromByteArray(buffer);
    }

    @Override
    public int read(byte[] b) throws IOException {
        int n = Math.min(b.length, size - position);
        if (n > 0) {
            System.arraycopy(content, position, b, 0, n);
            position += n;
        }
        return n;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public void truncate(long size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        this.size = (int) size;
        if (position > this.size) {
            position = this.size;
        }
    }

    @Override
    public InputStream inputStream(long start) throws IOException {
        checkPosition(position);
        return new ByteArrayInputStream(content, (int) start, (int) (size - start));
    }

    @Override
    public void flush() throws IOException {
        // it seems nothing to do
    }

    @Override
    public void close() throws IOException {
        // it seems nothing to do
    }

}