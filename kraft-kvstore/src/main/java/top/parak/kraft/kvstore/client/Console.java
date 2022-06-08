package top.parak.kraft.kvstore.client;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;
import top.parak.kraft.kvstore.client.cmd.*;

import java.util.*;

/**
 * KV-store client console.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class Console {

    /**
     * Console input prefix.
     */
    private static final String PROMPT = "kvstore-client " + KVStoreClient.VERSION + "> ";
    /**
     * Map to store command.
     * <p>
     * Key: {@link Command#getName()}, Value: {@link Command}.
     * </p>
     */
    private final Map<String, Command> commandMap;
    /**
     * Command context.
     */
    private final CommandContext commandContext;
    /**
     * Command line reader.
     */
    private final LineReader reader;

    /**
     * Create Console.
     *
     * @param serverMap server map
     */
    public Console(Map<NodeId, Address> serverMap) {
        commandMap = buildCommandMap(Arrays.asList(
                new ExitCommand(),
                new ClientAddServerCommand(),
                new ClientRemoveServerCommand(),
                new ClientListServerCommand(),
                new ClientGetLeaderCommand(),
                new ClientSetLeaderCommand(),
                new RaftAddNodeCommand(),
                new RaftRemoveNodeCommand(),
                new KVStoreGetCommand(),
                new KVStoreSetCommand()
        ));
        commandContext = new CommandContext(serverMap);

        ArgumentCompleter completer = new ArgumentCompleter(
                new StringsCompleter(commandMap.keySet()),
                new NullCompleter()
        );
        reader = LineReaderBuilder.builder()
                .completer(completer)
                .build();
    }


    private Map<String, Command> buildCommandMap(Collection<Command> commands) {
        Map<String, Command> commandMap = new HashMap<>();
        for (Command cmd : commands) {
            commandMap.put(cmd.getName(), cmd);
        }
        return commandMap;
    }

    /**
     * Start client console.
     */
    public void start() {
        commandContext.setRunning(true);
        showInfo();
        String line;
        while (commandContext.isRunning()) {
            try {
                line = reader.readLine(PROMPT);
                if (line.trim().isEmpty())
                    continue;
                dispatchCommand(line);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } catch (EndOfFileException ignored) {
                break;
            }
        }
    }

    /**
     * Show startup information.
     */
    private void showInfo() {
        System.out.println("Welcome to KRaft KVStore Shell\n");
        System.out.println("***********************************************");
        System.out.println("current server list: \n");
        commandContext.printSeverList();
        System.out.println("***********************************************");
    }

    /**
     * Dispatch command.
     *
     * @param line input
     */
    private void dispatchCommand(String line) {
        String[] commandNameAndArguments = line.split("\\s+", 2);
        String commandName = commandNameAndArguments[0];
        Command command = commandMap.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("no such command [" + commandName + "]");
        }
        command.execute(commandNameAndArguments.length > 1 ? commandNameAndArguments[1] : "", commandContext);
    }

}
