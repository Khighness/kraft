package top.parak.kraft.core.service;

/**
 * Thrown when channel occurs exception.
 *
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class ChannelException extends RuntimeException {

    public ChannelException() {
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

}
