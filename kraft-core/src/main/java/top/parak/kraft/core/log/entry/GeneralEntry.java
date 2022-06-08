package top.parak.kraft.core.log.entry;

/**
 * General log entry.
 * <p>
 * Used to store the command from clients.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class GeneralEntry extends AbstractEntry {

    /**
     * The bytes of the command.
     */
    private final byte[] commandBytes;

    /**
     * Create GeneralEntry.
     *
     * @param index        the index of the log entry
     * @param term         the term of the log entry
     * @param commandBytes the bytes of the command
     */
    public GeneralEntry(int index, int term, byte[] commandBytes) {
        super(KIND_GENERAL, index, term);
        this.commandBytes = commandBytes;
    }

    @Override
    public byte[] getCommandBytes() {
        return this.commandBytes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }

}
