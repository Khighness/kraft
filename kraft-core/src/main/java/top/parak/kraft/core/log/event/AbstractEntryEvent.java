package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.Entry;

/**
 * Abstract entry event.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public abstract class AbstractEntryEvent<T extends Entry> {

    /**
     * Entry type.
     */
    protected final T entry;

    /**
     * Create AbstractEntryEvent.
     *
     * @param entry entry
     */
    AbstractEntryEvent(T entry) {
        this.entry = entry;
    }

    /**
     * Get entry.
     *
     * @return entry
     */
    public T getEntry() {
        return entry;
    }

}
