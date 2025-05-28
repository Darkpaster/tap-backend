package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByCharacterId(Long characterId);
    Optional<Quest> findByCharacterIdAndQuest(Long characterId, String quest);

}
