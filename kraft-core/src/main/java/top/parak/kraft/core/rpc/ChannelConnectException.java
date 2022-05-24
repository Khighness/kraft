package top.parak.kraft.core.rpc;

import top.parak.kraft.core.service.ChannelException;

/**
 * Thrown when channel connection occur exception.
 *
 * @author KHighness
 * @since 2022-04-13
 * @email parakovo@gmail.com
 */
public class ChannelConnectException extends ChannelException {

    public ChannelConnectException(Throwable cause) {
        super(cause);
    }

    public ChannelConnectException(String message, Throwable cause) {
        super(message, cause);
    }

}
