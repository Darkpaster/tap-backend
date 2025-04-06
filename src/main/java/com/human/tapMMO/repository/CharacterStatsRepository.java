package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.CharacterStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CharacterStatsRepository extends JpaRepository<CharacterStats, Long> {

    Optional<CharacterStats> findByCharacterId(long characterId);
}
