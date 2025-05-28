package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByAccountId(Long accountId);
    Optional<Achievement> findByAccountIdAndName(Long accountId, String name);

}
