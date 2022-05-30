package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.core.service.NoAvailableServerException;
import top.parak.kraft.kvstore.client.CommandContext;

/**
 * RaftAddNode command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class RaftAddNodeCommand implements Command {

    @Override
    public String getName() {
        return "raft-add-node";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        // <node-id> <host> <raft-rpc-port>
        String[] pieces = arguments.split("\\s");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("usage: " + getName() + " <node-id> <host> <raft-rpc-port>");
        }

        String nodeId = pieces[0];
        String host = pieces[1];
        int servicePort;
        try {
            servicePort = Integer.parseInt(pieces[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("illegal port " + pieces[2] + "]");
        }

        try {
            context.getClient().addNode(nodeId, host, servicePort);
        } catch (NoAvailableServerException e) {
            System.err.println(e.getMessage());
        }
    }

}
