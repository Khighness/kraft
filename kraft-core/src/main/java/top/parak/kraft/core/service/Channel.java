package top.parak.kraft.core.service;

/**
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public interface Channel {

    /**
     * Send message.
     *
     * @param message message
     * @return response
     */
    Object send(Object message);

}
