package top.parak.kraft.core.service;

/**
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class ChannelException extends RuntimeException {

    public ChannelException() {
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

}
