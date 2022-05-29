package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;
import top.parak.kraft.kvstore.client.cmd.Command;

/**
 * ClientListServer command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class ClientListServerCommand implements Command {

    @Override
    public String getName() {
        return "client-list-server";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        context.printServerList();
    }

}
