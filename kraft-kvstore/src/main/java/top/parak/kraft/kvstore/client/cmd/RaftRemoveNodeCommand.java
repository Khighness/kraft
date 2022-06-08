package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.core.service.NoAvailableServerException;
import top.parak.kraft.kvstore.client.CommandContext;

/**
 * RaftRemoveNode command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class RaftRemoveNodeCommand implements Command {

    @Override
    public String getName() {
        return "raft-remove-node";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("usage: " + getName() + " <node-id>");
        }

        try {
            context.getClient().removeNode(arguments);
        } catch (NoAvailableServerException e) {
            System.err.println(e.getMessage());
        }
    }

}
