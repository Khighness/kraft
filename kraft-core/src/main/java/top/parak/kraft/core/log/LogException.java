package top.parak.kraft.core.log;

/**
 * Thrown when failed to operate log.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class LogException extends RuntimeException {

    public LogException() {
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(Throwable cause) {
        super(cause);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

}
