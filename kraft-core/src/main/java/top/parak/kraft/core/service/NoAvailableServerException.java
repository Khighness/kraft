package top.parak.kraft.core.service;

/**
 * Thrown when there is no available server.
 *
 * @author KHighness
 * @since 2022-05-01
 * @email parakovo@gmail.com
 */
public class NoAvailableServerException extends RuntimeException {

    public NoAvailableServerException(String message) {
        super(message);
    }

}
