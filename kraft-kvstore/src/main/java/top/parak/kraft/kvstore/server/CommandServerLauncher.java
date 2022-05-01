package top.parak.kraft.kvstore.server;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.parak.kraft.core.node.NodeEndpoint;

/**
 * Server launcher.
 *
 * @author KHighness
 * @since 2022-04-01
 * @email parakovo@gmail.com
 */
public class CommandServerLauncher {

    public static void main(String[] args) {

    }

    private static final Logger logger = LoggerFactory.getLogger(CommandServerLauncher.class);
    private static final String MODE_STANDALONE   = "standalone";
    private static final String MODE_STANDBY      = "standby";
    private static final String MODE_GROUP_MEMBER = "group-member";

    private volatile KVStoreServer rpcServer;

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
                .longOpt("port-raft-node")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .required()
                .desc("port of raft node, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p2")
                .longOpt("port-service")
                .hasArg().argName("port")
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
                .desc("group config, required when starts with group-member mode. format: <node-endpoint> <node-endpoint..., >" +
                        "format of node-endpoint: <node-id>, <host>, <port-raft-node>, eg: A, 127.0.0.1, 8001 B, 127.0.0.1, 8002")
                .build());

        if (args.length == 0) {
            DefaultParser parser = new DefaultParser();
            try {
                CommandLine cmdLine = parser.parse(options, args);
                String mode = cmdLine.getOptionValue("m", MODE_STANDALONE);
                switch (mode) {
                    case MODE_STANDALONE:
                        break;
                    case MODE_STANDBY:
                        break;
                    case MODE_GROUP_MEMBER:
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
        String host = cmdLine.getOptionValue('h', "localhost");
        int portRaftServer = ((Long) cmdLine.getParsedOptionValue("p1")).intValue();
        int portService = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        NodeEndpoint nodeEndpoint = new NodeEndpoint(id, host, portRaftServer);
    }

}
