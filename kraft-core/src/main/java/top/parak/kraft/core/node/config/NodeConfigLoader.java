package top.parak.kraft.core.node.config;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

/**
 * Node configuration loader.
 *
 * @author KHighness
 * @since 2022-06-05
 * @email parakovo@gmail.com
 */
public interface NodeConfigLoader {

    /**
     * Load input stream and return node config.
     *
     * @param input input stream
     * @return node config
     * @throws IOException if IO exception occurs
     */
    @Nonnull
    NodeConfig load(@Nonnull InputStream input) throws IOException;

}
