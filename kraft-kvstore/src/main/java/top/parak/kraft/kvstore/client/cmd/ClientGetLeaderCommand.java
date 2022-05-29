package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;
import top.parak.kraft.kvstore.client.cmd.Command;

/**
 * ClientGetLeader command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class ClientGetLeaderCommand implements Command {

    @Override
    public String getName() {
        return "client-get-leader";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        System.out.println(context.getClientLeaderId());
    }

}
