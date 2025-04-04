package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
