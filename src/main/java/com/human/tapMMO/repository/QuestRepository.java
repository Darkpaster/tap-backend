package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}
