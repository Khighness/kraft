package top.parak.kraft.kvstore.server;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.Node;
import top.parak.kraft.core.node.NodeBuilder;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Command server launcher.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class CommandServerLauncher {

    public static void main(String[] args) throws Exception {
        CommandServerLauncher launcher = new CommandServerLauncher();
        launcher.execute(args);
    }

    private static final Logger logger = LoggerFactory.getLogger(CommandServerLauncher.class);
    private static final String MODE_STANDALONE   = "standalone";
    private static final String MODE_STANDBY      = "standby";
    private static final String MODE_GROUP_MEMBER = "group-member";

    /**
     * KV-store server.
     */
    private volatile KVStoreServer server;

    /**
     * Parse args for server and start server.
     *
     * @param args arguments
     */
    private void execute(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("m")
                .hasArg()
                .argName("mode")
                .desc("start mode, available: standalone, standby, group-member. default is standalone")
                .build());
        options.addOption(Option.builder("i")
                .longOpt("id")
                .hasArg()
                .argName("node-id")
                .required()
                .desc("node id, required. must be unique in group. " +
                        "if starts with mode group-member, please ensure id in group config")
                .build());
        options.addOption(Option.builder("h")
                .hasArg()
                .argName("host")
                .desc("host, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p1")
                .longOpt("raft-rpc-port")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .desc("port of raft node, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p2")
                .longOpt("service-port")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .required()
                .desc("port of service, required")
                .build());
        options.addOption(Option.builder("d")
                .hasArg()
                .argName("data-dir")
                .desc("data directory, optional. must be present")
                .build());
        options.addOption(Option.builder("gc")
                .hasArgs()
                .argName("node-endpoint")
                .desc("group config, required when starts with group-member mode. format: <node-endpoint> <node-endpoint>..., " +
                        "format of node-endpoint: <node-id>,<host>,<port-raft-node>, eg: A,localhost,8000 B,localhost,8010")
                .build());

        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("kraft-kvstore [OPTION]...", options);
            return;
        }

        printBanner(true);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmdLine = parser.parse(options, args);
            String mode = cmdLine.getOptionValue('m', MODE_STANDALONE);
            switch (mode) {
                case MODE_STANDBY:
                    startAsStandaloneOrStandby(cmdLine, true);
                    break;
                case MODE_STANDALONE:
                    startAsStandaloneOrStandby(cmdLine, false);
                    break;
                case MODE_GROUP_MEMBER:
                    startAsGroupMember(cmdLine);
                    break;
                default:
                    throw new IllegalArgumentException("illegal mode [" + mode + "]");
            }
        } catch (ParseException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Start server with single node in standalone or standby mode.
     *
     * @param cmdLine    command line
     * @param standby    if standby
     * @throws Exception if args is invalid or server failed to start
     */
    private void startAsStandaloneOrStandby(CommandLine cmdLine, boolean standby) throws Exception {
        if (!cmdLine.hasOption("p1") || !cmdLine.hasOption("p2")) {
            throw new IllegalArgumentException("raft-rpc-port or service-port required");
        }

        String id = cmdLine.getOptionValue('i');
        String host = cmdLine.getOptionValue('h', "localhost");
        int raftRpcPort = ((Long) cmdLine.getParsedOptionValue("p1")).intValue();
        int servicePort = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        NodeEndpoint nodeEndpoint = new NodeEndpoint(id, host, raftRpcPort);
        Node node = new NodeBuilder(nodeEndpoint)
                .setStandby(standby)
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        KVStoreServer KVStoreServer = new KVStoreServer(node, new InetSocketAddress(host, servicePort));
        logger.info("id {}, start with mode {}", id, (standby ? "standby" : "standalone"));
        startServer(KVStoreServer);
    }

    /**
     * Start server as group member in group mode.
     *
     * @param cmdLine    command line
     * @throws Exception if args is invalid or server failed to start
     */
    private void startAsGroupMember(CommandLine cmdLine) throws Exception {
        if (!cmdLine.hasOption("gc")) {
            throw new IllegalArgumentException("group-config required");
        }

        String[] rawGroupConfigs = cmdLine.getOptionValues("gc");
        String rawNodeId = cmdLine.getOptionValue('i');
        String host = cmdLine.getOptionValue("h", "127.0.0.1");
        int servicePort = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        Set<NodeEndpoint> nodeEndpoints = Stream.of(rawGroupConfigs)
                .map(this::parseNodeEndpoint)
                .collect(Collectors.toSet());

        Node node = new NodeBuilder(nodeEndpoints, new NodeId(rawNodeId))
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        KVStoreServer KVStoreServer = new KVStoreServer(node, new InetSocketAddress(host, servicePort));
        logger.info("id {}, start as group member, group config {}", rawNodeId, nodeEndpoints);
        startServer(KVStoreServer);
    }

    /**
     * Parse raw group config and return node endpoint.
     *
     * @param rawGroupConfig raw group config
     * @return node endpoint
     */
    private NodeEndpoint parseNodeEndpoint(String rawGroupConfig) {
        String[] pieces = rawGroupConfig.split(",");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("illegal node endpoint [" + rawGroupConfig + "]");
        }
        String nodeId = pieces[0];
        String host = pieces[1];
        int port;
        try {
            port = Integer.parseInt(pieces[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("illegal port in node endpoint [" + rawGroupConfig + "]");
        }
        return new NodeEndpoint(nodeId, host, port);
    }

    /**
     * Start server.
     *
     * @param server     server
     * @throws Exception if server failed to start
     */
    private void startServer(KVStoreServer server) throws Exception {
        this.server = server;
        this.server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer, "shutdown"));
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print banner.
     *
     * @param enableBanner if true, console will print the banner
     */
    private void printBanner(boolean enableBanner) {
        System.out.println(
                "██╗  ██╗██████╗  █████╗ ███████╗████████╗\n" +
                "██║ ██╔╝██╔══██╗██╔══██╗██╔════╝╚══██╔══╝\n" +
                "█████╔╝ ██████╔╝███████║█████╗     ██║   \n" +
                "██╔═██╗ ██╔══██╗██╔══██║██╔══╝     ██║   \n" +
                "██║  ██╗██║  ██║██║  ██║██║        ██║   \n" +
                "╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝   ");
    }

}
