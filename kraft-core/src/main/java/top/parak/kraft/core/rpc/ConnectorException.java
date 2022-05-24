package top.parak.kraft.core.rpc;

/**
 * Thrown when connector occur IO exception,
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class ConnectorException extends RuntimeException {

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

}
