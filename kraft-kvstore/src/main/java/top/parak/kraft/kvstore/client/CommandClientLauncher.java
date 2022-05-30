package top.parak.kraft.kvstore.client;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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

    private static class ServerConfig {

        private final String nodeId;
        private final String host;
        private final int port;

        private ServerConfig(String nodeId, String host, int port) {
            this.nodeId = nodeId;
            this.host = host;
            this.port = port;
        }

        public String getNodeId() {
            return nodeId;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
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

    }

    private Map<NodeId, Address> parseServerConfig(String[] rawServerConfigList) {
        Map<NodeId, Address> serverConfig = new HashMap<>();
        for (String rawServerConfig : rawServerConfigList) {

        }
    }

    private ServerConfig parseServerConfig(String rawServerConfig) {

    }

}
