package top.parak.kraft.core.node.store;

import top.parak.kraft.core.node.NodeId;

import javax.annotation.Nullable;

/**
 * Node store.
 *
 * @author KHighness
 * @since 2022-03-20
 * @email parakovo@gmail.com
 */
public interface NodeStore {

    /**
     * Get term.
     *
     * @return term.
     */
    int getTerm();

    /**
     * Set term.
     *
     * @param term term
     */
    void setTerm(int term);

    /**
     * Get voted for.
     *
     * @return voted for
     */
    @Nullable
    NodeId getNotedFor();

    /**
     * Set voted for.
     *
     * @param nodeId voted for
     */
    void setVotedFor(@Nullable NodeId nodeId);

    /**
     * Close store.
     */
    void close();

}
