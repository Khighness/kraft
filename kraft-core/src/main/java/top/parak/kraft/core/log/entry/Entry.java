package top.parak.kraft.core.log.entry;

/**
 * Log Entry.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public interface Entry {

    /**
     * The kind of the empty log entry.
     */
    int KIND_NO_OP = 0;
    /**
     * The kind of the general log entry.
     */
    int KIND_GENERAL = 1;
    /**
     * The kind of the log entry for adding node.
     */
    int KIND_ADD_NODE = 3;
    /**
     * The kind of the log entry for removing node.
     */
    int KIND_REMOVE_NODE = 4;

    /**
     * Get the kind of log entry.
     *
     * @return the kind of the log entry
     */
    int getKind();

    /**
     * Get the index of log entry.
     *
     * @return the index of the log entry
     */
    int getIndex();

    /**
     * Get the term of log entry.
     *
     * @return the term of log entry.
     */
    int getTerm();

    /**
     * Get the metadata of log entry.
     *
     * @return metadata of log entry.
     */
    EntryMeta getMeta();

    /**
     * Get the payload of log entry.
     *
     * @return the payload of log entry
     */
    byte[] getCommandBytes();

}
