package top.parak.kraft.kvstore.client.cmd;

import com.google.common.annotations.Beta;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * <code>client-remove-server</code> command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
@Beta
public class ClientRemoveServerCommand implements Command {

    @Override
    public String getName() {
        return "client-remove-server";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("usage: " + getName() + " <node-id>");
        }

        if (context.clientRemoveServer(arguments)) {
            context.printSeverList();
        } else {
            System.err.println("no such server [" + arguments + "]");
        }
    }

}
