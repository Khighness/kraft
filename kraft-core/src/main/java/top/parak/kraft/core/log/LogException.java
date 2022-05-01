package top.parak.kraft.core.log;

/**
 * Thrown when failed to operate log.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class LogException extends RuntimeException {

    /**
     * Create LogException.
     */
    public LogException() {
    }

    /**
     * Create LogException.
     *
     * @param message message
     */
    public LogException(String message) {
        super(message);
    }

    /**
     * Create LogException.
     *
     * @param cause cause
     */
    public LogException(Throwable cause) {
        super(cause);
    }

    /**
     * Create LogException.
     *
     * @param message message
     * @param cause   cause
     */
    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

}
