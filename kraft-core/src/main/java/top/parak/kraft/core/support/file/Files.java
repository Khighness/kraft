package top.parak.kraft.core.support.file;

import java.io.File;
import java.io.IOException;

/**
 * File util.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class Files {

    /**
     * Create file.
     *
     * @param file file
     * @throws IOException if IO exception occurs
     */
    public static void touch(File file) throws IOException {
        if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("failed to touch file " + file);
        }
    }

}
