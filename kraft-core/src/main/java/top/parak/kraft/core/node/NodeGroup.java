package top.parak.kraft.core.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Node group.
 * <p>
 * Used to record the members in cluster and find members.
 * </p>
 *
 * @author KHighness
 * @since 2022-03-19
 * @email parakovo@gmail.com
 */
@NotThreadSafe
public class NodeGroup {

    private static final Logger logger = LoggerFactory.getLogger(NodeGroup.class);
    private final NodeId selfId;
    private Map<NodeId, GroupMember> memberMap;

    /**
     * Create group with single member(standalone).
     *
     * @param endpoint endpoint
     */
    NodeGroup(NodeEndpoint endpoint) {
        this(Collections.singleton(endpoint), endpoint.getId());
    }

    /**
     * Create group.
     *
     * @param endpoints endpoints
     * @param selfId    self id
     */
    NodeGroup(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.memberMap = buildMemberMap(endpoints);
        this.selfId = selfId;
    }

    /**
     * Build member map from endpoints.
     *
     * @param endpoints endpoints
     * @return member map
     */
    private Map<NodeId, GroupMember> buildMemberMap(Collection<NodeEndpoint> endpoints) {
        Map<NodeId, GroupMember> map = new HashMap<>();
        endpoints.forEach(endpoint -> map.put(endpoint.getId(), new GroupMember(endpoint)));
        if (map.isEmpty()) {
            throw new IllegalArgumentException("endpoints is empty");
        }
        return map;
    }

    /**
     * Get count of major.
     * <p>
     * For election.
     * </p>
     *
     * @return count
     * @see GroupMember#isMajor()
     */
    int getCountOfMajor() {
        return (int) memberMap.values().stream().filter(GroupMember::isMajor).count();
    }

    /**
     * Find self.
     *
     * @return self
     */
    @Nonnull
    GroupMember findSelf() {
        return findMember(selfId);
    }

    /**
     * Find member by id.
     * <p>
     * Thrown exception if member not found.
     * </p>
     *
     * @param id id
     * @return member, never be {@code null}
     */
    @Nonnull
    GroupMember findMember(NodeId id) {
        GroupMember member = memberMap.get(id);
        if (member == null) {
            throw new IllegalArgumentException("no such node " + id);
        }
        return member;
    }

    /**
     * Get member by id.
     *
     * @param id id
     * @return member, maybe {@code null}
     */
    @Nullable
    GroupMember getMember(NodeId id) {
        return memberMap.get(id);
    }

    /**
     * Check if node is major member.
     *
     * @param id id
     * @return true if member exists and member id major, otherwise false.
     */
    boolean isMemberOfMajor(NodeId id) {
        GroupMember member = memberMap.get(id);
        return member != null && member.isMajor();
    }

    /**
     * Upgrade member to major member.
     *
     * @param id id
     * @throws IllegalArgumentException if member not found
     * @see #findMember(NodeId)
     */
    void upgrade(NodeId id) {
        logger.info("upgrade node {}", id);
        findMember(id).setMajor(true);
    }

    /**
     * Downgrade member (set major to {@code false}).
     *
     * @param id id
     * @throws IllegalArgumentException if member not found
     */
    void downgrade(NodeId id) {
        logger.info("downgrade node {}", id);
        GroupMember member = findMember(id);
        member.setMajor(false);
        member.setRemoving();
    }

    /**
     * Check if member is unique one in group, in other word, check if standalone mode.
     *
     * @return true if only one member and the id of member equals to specified id, otherwise false
     */
    boolean isStandalone() {
        return memberMap.size() == 1 && memberMap.containsKey(selfId);
    }

}
