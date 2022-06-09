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
     * @return term
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
    NodeId getVotedFor();

    /**
     * Set voted for
     *
     * @param votedFor voted for
     */
    void setVotedFor(@Nullable NodeId votedFor);

    /**
     * Close store.
     */
    void close();

}
