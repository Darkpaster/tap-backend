package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.EquippedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquippedItemRepository extends JpaRepository<EquippedItem, Long> {

    Optional<List<EquippedItem>> findAllByCharacterId(long characterId);

    long deleteAllByCharacterId(long characterId);
}
