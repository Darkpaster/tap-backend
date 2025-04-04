package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.CharacterStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterStatsRepository extends JpaRepository<CharacterStats, Long> {
}
