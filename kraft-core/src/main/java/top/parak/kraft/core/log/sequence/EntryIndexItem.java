package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.EntryMeta;

import javax.annotation.concurrent.Immutable;

/**
 * Entry index item.
 *
 * @author KHighness
 * @since 2022-04-02
 * @email parakovo@gmail.com
 */
@Immutable
class EntryIndexItem {

    private final int index;
    private final long offset;
    private final int kind;
    private final int term;

    public EntryIndexItem(int index, long offset, int kind, int term) {
        this.index = index;
        this.offset = offset;
        this.kind = kind;
        this.term = term;
    }

    public int getIndex() {
        return index;
    }

    public long getOffset() {
        return offset;
    }

    public int getKind() {
        return kind;
    }

    public int getTerm() {
        return term;
    }

    EntryMeta toEntryMeta() {
        return new EntryMeta(kind, index, term);
    }

}
