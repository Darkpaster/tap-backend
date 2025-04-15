package com.human.tapMMO.service.game;

import com.human.tapMMO.model.tables.Group;
import com.human.tapMMO.repository.GroupMemberRepository;
import com.human.tapMMO.repository.GroupRepository;
import com.human.tapMMO.runtime.game.actor.player.Player;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public Group createGroup(String name, Player leader, GroupType groupType) {
        Group group = new Group();
        group.setName(name);
        group.setLeader(leader);
        group.setGroupType(groupType);

        // Добавляем лидера как участника группы
        group.getMembers().add(leader);

        return groupRepository.save(group);
    }

    @Transactional
    public void disbandGroup(Long groupId, Player player) {
        Group group = getGroupById(groupId);

        // Проверяем права
        if (!player.equals(group.getLeader())) {
            throw new ForbiddenException("Only group leader can disband the group");
        }

        // Удаляем группу
        groupRepository.delete(group);
    }

    @Transactional
    public void addMember(Long groupId, Long playerId, Player actor) {
        Group group = getGroupById(groupId);
        Player playerToAdd = playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем права
        if (!actor.equals(group.getLeader())) {
            throw new ForbiddenException("Only group leader can add members");
        }

        // Проверяем, не состоит ли игрок уже в группе
        if (group.getMembers().contains(playerToAdd)) {
            throw new IllegalArgumentException("Player is already in this group");
        }

// Проверяем, не превышено ли максимальное количество участников
        if (group.getMembers().size() >= group.getMaxMembers()) {
            throw new IllegalArgumentException("Group has reached maximum member capacity");
        }

        // Добавляем игрока в группу
        group.getMembers().add(playerToAdd);
        groupRepository.save(group);
    }

    @Transactional
    public void removeMember(Long groupId, Long playerId, Player actor) {
        Group group = getGroupById(groupId);
        Player playerToRemove = playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем, что игрок состоит в этой группе
        if (!group.getMembers().contains(playerToRemove)) {
            throw new IllegalArgumentException("Player is not a member of this group");
        }

        // Проверяем права: может удалить лидер или сам игрок может выйти
        if (!actor.equals(group.getLeader()) && !actor.equals(playerToRemove)) {
            throw new ForbiddenException("Insufficient permissions to remove member");
        }

        // Если удаляется лидер, то передаем лидерство другому игроку
        if (playerToRemove.equals(group.getLeader()) && group.getMembers().size() > 1) {
            Player newLeader = group.getMembers().stream()
                    .filter(p -> !p.equals(playerToRemove))
                    .findFirst()
                    .orElseThrow();

            group.setLeader(newLeader);
        } else if (playerToRemove.equals(group.getLeader()) && group.getMembers().size() <= 1) {
            // Если лидер последний в группе, то группа распускается
            groupRepository.delete(group);
            return;
        }

        // Удаляем игрока из группы
        group.getMembers().remove(playerToRemove);
        groupRepository.save(group);
    }

    @Transactional
    public void transferLeadership(Long groupId, Long newLeaderId, Player actor) {
        Group group = getGroupById(groupId);
        Player newLeader = playerRepository.findById(newLeaderId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем права
        if (!actor.equals(group.getLeader())) {
            throw new ForbiddenException("Only group leader can transfer leadership");
        }

        // Проверяем, что новый лидер состоит в группе
        if (!group.getMembers().contains(newLeader)) {
            throw new IllegalArgumentException("New leader is not a member of this group");
        }

        // Передаем лидерство
        group.setLeader(newLeader);
        groupRepository.save(group);
    }

    public List<Group> getPlayerGroups(Long playerId) {
        return groupRepository.findGroupsByMemberId(playerId);
    }

    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Group not found"));
    }

    public List<Group> findGroupsLookingForMore(GroupType groupType) {
        return groupRepository.findByIsLookingForMoreAndGroupType(true, groupType);
    }

    @Transactional
    public void setGroupLookingForMore(Long groupId, Boolean isLookingForMore, Player actor) {
        Group group = getGroupById(groupId);

        // Проверяем права
        if (!actor.equals(group.getLeader())) {
            throw new ForbiddenException("Only group leader can change group settings");
        }

        group.setIsLookingForMore(isLookingForMore);
        groupRepository.save(group);
    }
}