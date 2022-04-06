package top.parak.kraft.kvstore.support.toolkit;

/**
 * Runtime util.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class RuntimeUtil {

    private static final int CPUS = java.lang.Runtime.getRuntime().availableProcessors();

    /**
     * Returns the number of processors available to the Java virtual machine.
     *
     * @return an approximation to the total amount of memory currently
     *         available for future allocated objects, measured in bytes.
     */
    public static int cpus() {
        return CPUS;
    }

}
