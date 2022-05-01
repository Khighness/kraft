package top.parak.kraft.core.log.entry;

/**
 * Metadata of log entry.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class EntryMeta {

    /**
     * The kind of the log entry.
     */
    private final int kind;

    /**
     * The index of the log entry.
     */
    private final int index;

    /**
     * The term of the log entry.
     */
    private final int term;

    /**
     * Create EntryMeta.
     *
     * @param kind  the kind of the log entry
     * @param index the index of the log entry
     * @param term  the term of the log entry
     */
    public EntryMeta(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    /**
     * Get kind from metadata.
     *
     * @return the kind of the log entry
     */
    public int getKind() {
        return kind;
    }

    /**
     * Get index from metadata.
     *
     * @return the index of the log entry
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get term from metadata.
     *
     * @return the term of the log entry
     */
    public int getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "EntryMeta{" +
                "kind=" + kind +
                ", index=" + index +
                ", term=" + term +
                '}';
    }

}
