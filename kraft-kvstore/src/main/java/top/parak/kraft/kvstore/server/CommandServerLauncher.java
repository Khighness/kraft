package top.parak.kraft.kvstore.server;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.parak.kraft.core.node.Node;
import top.parak.kraft.core.node.NodeBuilder;
import top.parak.kraft.core.node.NodeEndpoint;
import top.parak.kraft.core.node.NodeId;

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
        CommandServerLauncher commandServerLauncher = new CommandServerLauncher();
        commandServerLauncher.execute(args);
    }

    private static final Logger logger = LoggerFactory.getLogger(CommandServerLauncher.class);
    private static final String MODE_STANDALONE   = "standalone";
    private static final String MODE_STANDBY      = "standby";
    private static final String MODE_GROUP_MEMBER = "group-member";

    private volatile KVStoreServer server;

    private void execute(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("m")
                .hasArg()
                .argName("mode")
                .desc("start mode, available: standalone, standby, group-member, default is standalone")
                .build());
        options.addOption(Option.builder("i")
                .longOpt("id")
                .hasArg()
                .required()
                .desc("node id, required. must be unique in group. " +
                        "if starts with mode group-member, please ensure id in group config")
                .build());
        options.addOption(Option.builder("p1")
                .longOpt("raft-rpc-port")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .desc("port of raft rpc, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p2")
                .longOpt("service-port")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .required()
                .desc("port of service. required")
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
                        "format of node-endpoint: <node-id>,<host>,<raft-rpc-port>, e.g: A,127.0.0.1,8001 B,127.0.0.1,8002")
                .build());

        if (args.length != 0) {
            DefaultParser parser = new DefaultParser();
            try {
                CommandLine cmdLine = parser.parse(options, args);
                String mode = cmdLine.getOptionValue("m", MODE_STANDALONE);
                switch (mode) {
                    case MODE_STANDALONE:
                        startAsStandaloneOrStandby(cmdLine, true);
                        break;
                    case MODE_STANDBY:
                        startAsStandaloneOrStandby(cmdLine, false);
                        break;
                    case MODE_GROUP_MEMBER:
                        startAsGroupMember(cmdLine);
                        break;
                }
            } catch (ParseException | IllegalArgumentException e) {
                logger.error("execute cmd error: {}", e.getMessage());
            }
        }
    }

    private void startAsStandaloneOrStandby(CommandLine cmdLine, boolean standby) throws Exception {
        if (!cmdLine.hasOption("p1") || !cmdLine.hasOption("p2")) {
            throw new IllegalArgumentException("port-raft-node or port-service required");
        }
        String id = cmdLine.getOptionValue('i');
        String host = cmdLine.getOptionValue('h', "127.0.0.1");
        int raftRpcPort = ((Long) cmdLine.getParsedOptionValue("p1")).intValue();
        int servicePort = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        NodeEndpoint nodeEndpoint = new NodeEndpoint(id, host, raftRpcPort);
        Node node = new NodeBuilder(nodeEndpoint)
                .setStandby(standby)
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        KVStoreServer server = new KVStoreServer(node, servicePort);
        logger.info("node {} is serving at {}:{} with mode {}, raft rpc port {}",
                (standby ? "standby" : "standalone"), id, host, servicePort, raftRpcPort);
        startServer(server);
    }

    private void startAsGroupMember(CommandLine cmdLine) throws Exception {
        if (!cmdLine.hasOption("gc")) {
            throw new IllegalArgumentException("group-config required");
        }

        String[] rawGroupConfig = cmdLine.getOptionValues("gc");
        String id = cmdLine.getOptionValue("i");
        String host = cmdLine.getOptionValue('h', "127.0.0.1");
        int servicePort = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        Set<NodeEndpoint> nodeEndpoints = Stream.of(rawGroupConfig).map(this::parseNodeEndpoint)
                .collect(Collectors.toSet());

        Node node = new NodeBuilder(nodeEndpoints, new NodeId(id))
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        KVStoreServer server = new KVStoreServer(node, servicePort);
        logger.info("node {} is serving at {}:{} as group member, group config {}", id, host, servicePort, nodeEndpoints);
        startServer(server);
    }

    private NodeEndpoint parseNodeEndpoint(String rawNodeEndpoint) {
        String[] pieces = rawNodeEndpoint.split(",");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("illegal node endpoint [" + rawNodeEndpoint + "]");
        }
        String nodeId = pieces[0];
        String host = pieces[1];
        int raftRpcPort;
        try {
            raftRpcPort = Integer.parseInt(pieces[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("illegal port in node endpoint [" + rawNodeEndpoint + "]");
        }
        return new NodeEndpoint(nodeId, host, raftRpcPort);
    }

    private void startServer(KVStoreServer server) throws Exception {
        this.server = server;
        this.server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer, "shutdown"));
    }

    private void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
