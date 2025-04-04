package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
