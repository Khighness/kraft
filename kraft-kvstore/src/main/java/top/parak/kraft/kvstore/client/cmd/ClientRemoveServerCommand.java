package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * ClientRemoveServer command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
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
            context.printServerList();
        } else {
            System.out.println("no such server [" + arguments + "]");
        }
    }

}
