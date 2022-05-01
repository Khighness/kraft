package top.parak.kraft.core.log;

import java.io.File;

/**
 * Log directory.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public interface LogDir {

    /**
     * Initialize directory.
     */
    void initialize();

    /**
     * Check if directory exists.
     *
     * @return true if directory exists, otherwise false.
     */
    boolean exists();

    /**
     * Get the log entry file.
     *
     * @return log entry file
     */
    File getEntriesFile();

    /**
     * Get the log entry index file corresponding to the log entry file.
     *
     * @return log entry index file
     */
    File getEntryOffsetIndexFile();

    /**
     * Get snapshot file.
     *
     * @return snapshot file
     */
    File getSnapshotFile();

    /**
     * Get directory.
     *
     * @return directory
     */
    File get();

    /**
     * Rename directory.
     *
     * @param logDir newName
     * @return true if succeeded, otherwise false
     */
    boolean renameTo(LogDir logDir);

}
