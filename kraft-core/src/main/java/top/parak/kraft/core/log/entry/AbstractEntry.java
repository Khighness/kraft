package top.parak.kraft.core.log.entry;

/**
 * Abstract log entry.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public abstract class AbstractEntry implements Entry {

    /**
     * The kind of the log entry.
     */
    private final int kind;
    /**
     * The index of the log entry.
     */
    protected final int index;
    /**
     * The term of the log entry.
     */
    protected final int term;

    /**
     * Create AbstractEntry.
     *
     * @param kind  the kind of the log entry
     * @param index the index of the log entry
     * @param term  the term of the log entry
     */
    public AbstractEntry(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    @Override
    public int getKind() {
        return this.kind;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getTerm() {
        return this.term;
    }

    @Override
    public EntryMeta getMeta() {
        return new EntryMeta(kind, index, term);
    }

}
