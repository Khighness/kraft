package top.parak.kraft.core.log.event;

/**
 * Snapshot generate event.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class SnapshotGenerateEvent {

    private final int lastIncludedIndex;

    public SnapshotGenerateEvent(int lastIncludedIndex) {
        this.lastIncludedIndex = lastIncludedIndex;
    }

    public int getLastIncludedIndex() {
        return lastIncludedIndex;
    }

}
