package top.parak.kraft.kvstore.client.cmd;

import com.google.common.annotations.Beta;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * <code>client-get-leader</code> command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
@Beta
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
