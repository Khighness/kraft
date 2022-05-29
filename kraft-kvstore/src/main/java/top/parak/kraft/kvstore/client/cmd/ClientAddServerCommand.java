package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;
import top.parak.kraft.kvstore.client.cmd.Command;

/**
 * ClientAddServer command.
 *
 * @author KHighness
 * @since 2022-05-29
 * @email parakovo@gmail.com
 */
public class ClientAddServerCommand implements Command {

    @Override
    public String getName() {
        return "client-add-server";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        // <node-id> <host> <port>
        String[] pieces = arguments.split("\\s");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("usage: " + getName() + "<node-id> <host> <port>");
        }

        String nodeId = pieces[0];
        String host = pieces[1];
        int port;
        try {
            port = Integer.parseInt(pieces[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("illegal port [" + pieces[2] + "]");
        }


        context.clientAddServer(nodeId, host, port);
        context.printServerList();
    }

}
