package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

public class GroupConfigEntryFromLeaderAppendEvent extends AbstractEntryEvent<GroupConfigEntry> {

    public GroupConfigEntryFromLeaderAppendEvent(GroupConfigEntry entry) {
        super(entry);
    }

}
