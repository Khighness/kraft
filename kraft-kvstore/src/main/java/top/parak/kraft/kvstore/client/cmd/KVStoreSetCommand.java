package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * <code>kvstore-set</code> command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class KVStoreSetCommand implements Command {

    @Override
    public String getName() {
        return "kvstore-set";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        int index = arguments.indexOf(' ');
        if (index <= 0 || index == arguments.length() - 1) {
            throw new IllegalArgumentException("usage: " + getName() + " <key> <value>");
        }
        context.getClient().set(arguments.substring(0, index), arguments.substring(index + 1).getBytes());
    }

}
