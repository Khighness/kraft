package top.parak.kraft.kvstore.client.cmd;

import top.parak.kraft.kvstore.client.CommandContext;

/**
 * KVStoreSet command.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class KVStoreSetCommand implements Command {

    @Override
    public String getName() {
        return "kvstore-set";
    }

    @Override
    public void execute(String arguments, CommandContext context) {
        String[] pieces = arguments.split("\\s");
        if (pieces.length != 2) {
            throw new IllegalArgumentException("usage: " + getName() + " <key> <value>");
        }

        String key = pieces[0];
        String value = pieces[1];

        byte[] valueBytes = value.getBytes();
        context.getClient().set(key, valueBytes);
    }

}
