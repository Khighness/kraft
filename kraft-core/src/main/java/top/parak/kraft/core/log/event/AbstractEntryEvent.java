package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.Entry;

/**
 * Abstract entry event.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
class AbstractEntryEvent<T extends Entry> {

    protected final T entry;

    AbstractEntryEvent(T entry) {
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

}
