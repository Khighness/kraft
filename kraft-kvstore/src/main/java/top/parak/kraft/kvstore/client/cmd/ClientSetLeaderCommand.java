package top.parak.kraft.kvstore.client.cmd;

import com.google.common.annotations.Beta;

import top.parak.kraft.core.node.NodeId;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * ClientSetLeader command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
@Beta
public class ClientSetLeaderCommand implements Command {

    @Override
    public String getName() {
        return "client-set-leader";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("usage: " + getName() + " <node-id>");
        }

        NodeId nodeId = new NodeId(arguments);
        try {
            context.setClientLeader(nodeId);
            System.out.println(nodeId);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }

}
