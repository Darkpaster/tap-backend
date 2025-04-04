package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
