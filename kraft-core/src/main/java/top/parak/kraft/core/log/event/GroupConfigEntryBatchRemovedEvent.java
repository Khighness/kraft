package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

public class GroupConfigEntryBatchRemovedEvent {

    private final GroupConfigEntry firstRemovedEntry;

    public GroupConfigEntryBatchRemovedEvent(GroupConfigEntry firstRemovedEntry) {
        this.firstRemovedEntry = firstRemovedEntry;
    }

    public GroupConfigEntry getFirstRemovedEntry() {
        return firstRemovedEntry;
    }

}
