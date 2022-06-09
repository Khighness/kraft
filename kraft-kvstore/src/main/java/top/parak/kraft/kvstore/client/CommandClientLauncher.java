package top.parak.kraft.kvstore.client;

import org.apache.commons.cli.*;

import top.parak.kraft.core.node.NodeId;
import top.parak.kraft.core.rpc.Address;

import java.util.HashMap;
import java.util.Map;

/**
 * Command client launcher.
 *
 * @author KHighness
 * @since 2022-05-30
 * @email parakovo@gmail.com
 */
public class CommandClientLauncher {

    public static void main(String[] args) throws Exception {
        CommandClientLauncher launcher = new CommandClientLauncher();
        launcher.execute(args);
    }

    /**
     * Server config.
     */
    private static class ServerConfig {

        private final String nodeId;
        private final String host;
        private final int port;

        ServerConfig(String nodeId, String host, int port) {
            this.nodeId = nodeId;
            this.host = host;
            this.port = port;
        }

        String getNodeId() {
            return nodeId;
        }

        String getHost() {
            return host;
        }

        int getPort() {
            return port;
        }

    }

    /**
     * Parse args for client and start console.
     *
     * @param args arguments
     */
    private void execute(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("sc")
                .hasArgs()
                .argName("server-config")
                .required()
                .desc("server config, required. format: <server-config> <server-config> ..., " +
                        "format of server-config: <node-id>,<host>,<service-port>, " +
                        "e.g: A,127.0.0.1,3331 B,127.0.0.1,3332 C,127.0.0.1,3333")
                .build());
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("kraft-kvstore-client [OPTION]...", options);
            return;
        }

        CommandLineParser parser = new DefaultParser();
        Map<NodeId, Address> serverMap;
        try {
            CommandLine commandLine = parser.parse(options, args);
            serverMap = parseServerConfig(commandLine.getOptionValues("sc"));
        } catch (ParseException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        Console console = new Console(serverMap);
        console.start();
    }

    /**
     * Parse server config list and return server config map.
     *
     * @param rawServerConfigs raw server config list
     * @return map
     */
    private Map<NodeId, Address> parseServerConfig(String[] rawServerConfigs) {
        Map<NodeId, Address> serverMap = new HashMap<>();
        for (String rawServerConfig : rawServerConfigs) {
            ServerConfig serverConfig = parseServerConfig(rawServerConfig);
            serverMap.put(new NodeId(serverConfig.getNodeId()), new Address(serverConfig.getHost(), serverConfig.getPort()));
        }
        return serverMap;
    }

    /**
     * Parse server config raw ad return server config.
     *
     * @param rawServerConfig raw server config
     * @return server config
     */
    private ServerConfig parseServerConfig(String rawServerConfig) {
        String[] pieces = rawServerConfig.split(",");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("illegal server config [" + rawServerConfig + "]");
        }
        String nodeId = pieces[0];
        String host = pieces[1];
        int servicePort;
        try {
            servicePort = Integer.parseInt(pieces[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("illegal port [" + pieces[2] + "]");
        }
        return new ServerConfig(nodeId, host, servicePort);
    }

}
