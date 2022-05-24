package top.parak.kraft.core.service;

import top.parak.kraft.core.node.NodeId;

/**
 * @author KHighness
 * @since 2022-03-30
 * @email parakovo@gmail.com
 */
public class RedirectException extends ChannelException {

    private final NodeId leaderId;

    public RedirectException(NodeId leaderId) {
        this.leaderId = leaderId;
    }

    public NodeId getLeaderId() {
        return leaderId;
    }

}
