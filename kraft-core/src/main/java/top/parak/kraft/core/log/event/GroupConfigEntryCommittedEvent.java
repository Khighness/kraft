package top.parak.kraft.core.log.event;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

/**
 * Group config entry committed event.
 *
 * @author KHighness
 * @since 2022-04-07
 * @email parakovo@gmail.com
 */
public class GroupConfigEntryCommittedEvent extends AbstractEntryEvent<GroupConfigEntry> {

    public GroupConfigEntryCommittedEvent(GroupConfigEntry entry) {
        super(entry);
    }

}
