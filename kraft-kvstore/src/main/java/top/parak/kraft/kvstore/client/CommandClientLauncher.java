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

    public static void main(String[] args) {
        CommandClientLauncher commandClientLauncher = new CommandClientLauncher();
        commandClientLauncher.execute(args);
    }

    private static class ServerConfig {

        private final String nodeId;
        private final String host;
        private final int servicePort;

        private ServerConfig(String nodeId, String host, int servicePort) {
            this.nodeId = nodeId;
            this.host = host;
            this.servicePort = servicePort;
        }

        public String getNodeId() {
            return nodeId;
        }

        public String getHost() {
            return host;
        }

        public int getServicePort() {
            return servicePort;
        }

    }

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
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("kraft-kvstore-client [OPTION]...", options);
            return;
        }

        DefaultParser parser = new DefaultParser();
        Map<NodeId, Address> serverMap;
        try {
            CommandLine commandLine = parser.parse(options, args);
            serverMap = parseServerConfig(commandLine.getOptionValues("sc"));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        }

        Console console = new Console(serverMap);
        console.start();
    }

    private Map<NodeId, Address> parseServerConfig(String[] rawServerConfigList) {
        Map<NodeId, Address> serverMap = new HashMap<>();
        for (String rawServerConfig : rawServerConfigList) {
            ServerConfig serverConfig = parseServerConfig(rawServerConfig);
            serverMap.put(new NodeId(serverConfig.getNodeId()), new Address(serverConfig.getHost(), serverConfig.getServicePort()));
        }
        return serverMap;
    }

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
