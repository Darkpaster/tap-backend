package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByItemId(long itemId);

    Optional<List<InventoryItem>> findAllByCharacterId(long characterId);

    long deleteAllByCharacterId(long characterId);

    long deleteByItemId(long itemId);
}
