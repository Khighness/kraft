package top.parak.kraft.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Root dir.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
class RootDir {

    private static final Logger logger = LoggerFactory.getLogger(RootDir.class);

    /**
     * The name of the log snapshot file.
     */
    static final String FILE_NAME_SNAPSHOT = "service.ss";
    /**
     * The name of the log entry file.
     */
    static final String FILE_NAME_ENTRIES = "entries.bin";
    /**
     * The name of the log entry meta file.
     */
    static final String FILE_NAME_ENTRY_OFFSET_INDEX = "entries.idx";

    private static final String DIR_NAME_GENERATING = "generating";
    private static final String DIR_NAME_INSTALLING = "installing";

    private final File baseDir;

    RootDir(File baseDir) {
        if (!baseDir.exists()) {
            throw new IllegalArgumentException("dir " + baseDir + " not exists");
        }
        this.baseDir = baseDir;
    }

    LogDir getLogDirForGenerating() {
        return getOrCreateNormalLogDir(DIR_NAME_GENERATING);
    }

    LogDir getLogDirForInstalling() {
        return getOrCreateNormalLogDir(DIR_NAME_INSTALLING);
    }

    private NormalLogDir getOrCreateNormalLogDir(String name) {
        NormalLogDir logDir = new NormalLogDir(new File(baseDir, name));
        if (!logDir.exists()) {
            logDir.initialize();
        }
        return logDir;
    }

    LogDir rename(LogDir dir, int lastIncludedIndex) {
        LogGeneration destDir = new LogGeneration(baseDir, lastIncludedIndex);
        if (destDir.exists()) {
            throw new IllegalStateException("failed to rename, destination dir " + destDir + " exists");
        }

        logger.info("rename dir {} to {}", dir, destDir);
        if (!dir.renameTo(destDir)) {
            throw new IllegalStateException("failed to rename " + dir + " to " + destDir);
        }
        return destDir;
    }

    LogGeneration createFirstGeneration() {
        LogGeneration logGeneration = new LogGeneration(baseDir, 0);
        logGeneration.initialize();
        return logGeneration;
    }

    LogGeneration getLatestGeneration() {
        File[] files = baseDir.listFiles();
        if (files == null) {
            return null;
        }
        LogGeneration latest = null;
        String filename;
        LogGeneration logGeneration;
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            filename = file.getName();
            if (DIR_NAME_GENERATING.equals(filename) || DIR_NAME_INSTALLING.equals(filename) || !LogGeneration.isValidDirName(filename)) {
                continue;
            }
            logGeneration = new LogGeneration(file);
            if (latest == null || logGeneration.compareTo(latest) > 0) {
                latest = logGeneration;
            }
        }
        return latest;
    }

}
