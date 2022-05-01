package top.parak.kraft.core.log.sequence;

import top.parak.kraft.core.log.entry.GroupConfigEntry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Group config entry list.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class GroupConfigEntryList implements Iterable<GroupConfigEntry> {

    private final LinkedList<GroupConfigEntry> entries = new LinkedList<>();

    /**
     * Get last group config entry.
     *
     * @return last group config entry
     */
    public GroupConfigEntry getLast() {
        return entries.isEmpty() ? null : entries.getLast();
    }

    /**
     * Add a group config entry.
     *
     * @param entry the group config entry
     */
    public void add(GroupConfigEntry entry) {
        entries.add(entry);
    }

    /**
     * Remove entries whose index is greater than the specified index.
     *
     * @param entryIndex the specified index
     * @return the first removed entry, {@code null} if no entry removed
     */
    public GroupConfigEntry removeAfter(int entryIndex) {
        Iterator<GroupConfigEntry> iterator = entries.iterator();
        GroupConfigEntry firstRemovedEntry = null;
        while (iterator.hasNext()) {
            GroupConfigEntry entry = iterator.next();
            if (entry.getIndex() > entryIndex) {
                if (entry.getIndex() > entryIndex) {
                    firstRemovedEntry = entry;
                }
                iterator.remove();
            }
        }
        return firstRemovedEntry;
    }

    /**
     * Return entries whose index is greater than {@code fromIndex} and less than {@code toIndex}.
     *
     * @param fromIndex from index
     * @param toIndex   to index
     * @return entries [fromIndex, toIndex)
     */
    public List<GroupConfigEntry> subList(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("from index > to index");
        }
        return entries.stream()
                .filter(e -> e.getIndex() >= fromIndex && e.getIndex() < toIndex)
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<GroupConfigEntry> iterator() {
        return entries.iterator();
    }

    @Override
    public String toString() {
        return "GroupConfigEntryList{" +
                "entries=" + entries +
                '}';
    }

}
