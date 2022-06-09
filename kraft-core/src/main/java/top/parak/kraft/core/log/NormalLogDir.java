package top.parak.kraft.core.log;

import java.io.File;

/**
 * Normal log directory.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class NormalLogDir extends AbstractLogDir {

    /**
     * Create NormalLogDir.
     *
     * @param dir directory
     */
    NormalLogDir(File dir) {
        super(dir);
    }

    @Override
    public String toString() {
        return "NormalLogDir{" +
                "dir=" + dir +
                '}';
    }

}
