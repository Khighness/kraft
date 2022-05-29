package top.parak.kraft.kvstore.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import top.parak.kraft.kvstore.client.cmd.Command;

/**
 * Encapsulate client's command and netty's chanel.
 * @see Command
 * @see Channel
 *
 * @author KHighness
 * @since 2022-03-14
 * @email parakovo@gmail.com
 */
public class CommandRequest<T> {

    private final T command;
    private final Channel channel;

    public CommandRequest(T command, Channel channel) {
        this.command = command;
        this.channel = channel;
    }

    public void reply(Object response) {
        this.channel.write(response);
    }

    public void addCloseListener(Runnable runnable) {
        this.channel.closeFuture().addListener(
                (ChannelFutureListener) future -> runnable.run()
        );
    }

    public T getCommand() {
        return command;
    }

}
