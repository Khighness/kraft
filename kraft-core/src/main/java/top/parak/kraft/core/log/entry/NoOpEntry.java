package top.parak.kraft.core.log.entry;

/**
 * No-operation log entry.
 * <p>
 * Used as the first log entry added by the elected new leader.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class NoOpEntry extends AbstractEntry {

    /**
     * Create NoOpEntry.
     *
     * @param index the index of the log entry
     * @param term  the term of the log entry
     */
    public NoOpEntry(int index, int term) {
        super(KIND_NO_OP, index, term);
    }

    @Override
    public byte[] getCommandBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "NoOpEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }

}
