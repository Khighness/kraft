package top.parak.kraft.core.node.task;

import top.parak.kraft.core.node.NodeEndpoint;

/**
 * AddNode task.
 *
 * @author KHighness
 * @since 2022-05-31
 * @email parakovo@gmail.com
 */
public class AddNodeTask extends AbstractGroupConfigChangeTask {

    private final NodeEndpoint endpoint;
    private final int nextIndex;
    private final int matchIndex;

    public AddNodeTask(NodeEndpoint endpoint, int nextIndex, int matchIndex) {
        this.endpoint = endpoint;
        this.nextIndex = nextIndex;
        this.matchIndex = matchIndex;
    }
}
