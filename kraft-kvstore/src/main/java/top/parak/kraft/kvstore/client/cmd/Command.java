package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * Command.
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public interface Command {

    /**
     * Get th name of command.
     *
     * @return command name
     */
    String getName();

    /**
     * Execute the command.
     *
     * @param arguments arguments
     * @param context command context
     */
    void execute(String arguments, CommandContext context);

}
