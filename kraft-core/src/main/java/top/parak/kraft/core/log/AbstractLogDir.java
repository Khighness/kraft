package top.parak.kraft.core.log;

import top.parak.kraft.core.support.file.Files;

import java.io.File;
import java.io.IOException;

/**
 * @author KHighness
 * @email parakovo@gmail.com
 * @since 2022-04-01
 */
abstract class AbstractLogDir implements LogDir {

    /**
     * The log directory.
     */
    final File dir;

    /**
     * Create AbstractLogDir.
     *
     * @param dir directory
     */
    AbstractLogDir(File dir) {
        this.dir = dir;
    }

    @Override
    public void initialize() {
        if (!dir.exists() && !dir.mkdir()) {
            throw new LogException("failed to create directory: " + dir);
        }
        try {
            Files.touch(getEntriesFile());
            Files.touch(getEntryOffsetIndexFile());
        } catch (IOException e) {
            throw new LogException("failed to create file", e);
        }
    }

    @Override
    public boolean exists() {
        return dir.exists();
    }

    @Override
    public File getSnapshotFile() {
        return new File(dir, RootDir.FILE_NAME_SNAPSHOT);
    }

    @Override
    public File getEntriesFile() {
        return new File(dir, RootDir.FILE_NAME_ENTRIES);
    }

    @Override
    public File getEntryOffsetIndexFile() {
        return new File(dir, RootDir.FILE_NAME_ENTRY_OFFSET_INDEX);
    }

    @Override
    public File get() {
        return dir;
    }

    @Override
    public boolean renameTo(LogDir logDir) {
        return dir.renameTo(logDir.get());
    }

}
