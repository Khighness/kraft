package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

public class GroupConfigEntryCommittedEvent extends AbstractEntryEvent<GroupConfigEntry> {

    public GroupConfigEntryCommittedEvent(GroupConfigEntry entry) {
        super(entry);
    }

}
