package top.parak.kraft.core.rpc.nio;

/**
 * Thrown when connector occurs exception.
 *
 * @author KHighness
 * @since 2022-04-14
 * @email parakovo@gmail.com
 */
public class ConnectorException extends RuntimeException {

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

}
