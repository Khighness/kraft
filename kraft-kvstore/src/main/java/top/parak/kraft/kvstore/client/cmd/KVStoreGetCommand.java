package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.core.service.NoAvailableServerException;
import top.parak.kraft.kvstore.client.CommandContext;

/**
 * KVStoreGet command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class KVStoreGetCommand implements Command {

    @Override
    public String getName() {
        return "kvstore-get";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Usage: " + getName() + "  <key>");
        }


        byte[] valueBytes;
        try {
            valueBytes = context.getClient().get(arguments);
        } catch (NoAvailableServerException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (valueBytes == null) {
            System.out.println("null");
        } else {
            System.out.println(new String(valueBytes));
        }
    }

}
