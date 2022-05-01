package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

/**
 * Group config entry batch removed event.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class GroupConfigEntryBatchRemovedEvent {

    private final GroupConfigEntry firstRemovedEntry;

    public GroupConfigEntryBatchRemovedEvent(GroupConfigEntry firstRemovedEntry) {
        this.firstRemovedEntry = firstRemovedEntry;
    }

    public GroupConfigEntry getFirstRemovedEntry() {
        return firstRemovedEntry;
    }

}
