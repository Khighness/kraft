package top.parak.kraft.kvstore.client.cmd;

import com.google.common.annotations.Beta;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * ClientListServer command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
@Beta
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
