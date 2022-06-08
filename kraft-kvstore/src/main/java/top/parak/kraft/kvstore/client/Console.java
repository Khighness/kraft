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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Client console,
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class Console {

    private static final String PROMPT = "kvstore-client " + KVStoreClient.VERSION + "> ";
    private final Map<String, Command> commandMap;
    private final CommandContext commandContext;
    private final LineReader lineReader;

    public Console(Map<NodeId, Address> serverMap) {
        commandMap = buildCommandMap(Arrays.asList(
                new ExitCommand(),
                new KVStoreGetCommand(),
                new KVStoreSetCommand(),
                new RaftAddNodeCommand(),
                new RaftRemoveNodeCommand()
        ));

        commandContext = new CommandContext(serverMap);

        ArgumentCompleter completer = new ArgumentCompleter(
                new StringsCompleter(commandMap.keySet()),
                new NullCompleter()
        );
        lineReader = LineReaderBuilder.builder()
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

    public void start() {
        commandContext.setRunning(true);
        showInfo();
        String line;
        while (commandContext.isRunning()) {
            try {
                line = lineReader.readLine(PROMPT);
                if (line.trim().isEmpty()) {
                    continue;
                }
                dispatchCommand(line);
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            } catch (EndOfFileException ignored) {
                break;
            }
        }
    }

    private void showInfo() {
        System.out.println("Welcome to KRaft KVStore Shell\n");
        System.out.println("***********************************************");
        System.out.println("current server list");
        commandContext.printServerList();
        System.out.println("***********************************************");
    }

    private void dispatchCommand(String line) {
        String[] commandAndArguments = line.split("\\s+", 2);
        String commandName = commandAndArguments[0];
        Command command = commandMap.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("no such command [" + commandName + "]");
        }
        command.execute(commandAndArguments.length > 1 ? commandAndArguments[1] : "", commandContext);
    }

}
