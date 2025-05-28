package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByCharacterIdAndSkillName(Long characterId, String skillName);
}
