package top.parak.kraft.core.log.statemachine;

/**
 * Thrown when state machine occurs exception.
 *
 * @author KHighness
 * @since 2022-03-31
 * @email parakovo@gmail.com
 */
public class StatemachineException extends RuntimeException {

    public StatemachineException(Throwable cause) {
        super(cause);
    }

    public StatemachineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
