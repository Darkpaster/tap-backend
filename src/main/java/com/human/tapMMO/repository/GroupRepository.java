package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupLeaderId(Long leaderId);

//    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :playerId")
//    List<Group> findGroupsByMemberId(Long playerId);
//
//    List<Group> findByIsLookingForMoreAndGroupType(Boolean isLookingForMore, GroupType groupType);
}
