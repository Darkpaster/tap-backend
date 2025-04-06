package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.ItemPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemPositionRepository extends JpaRepository<ItemPosition, Long> {

    Optional<ItemPosition> getByItemId(long itemId);
}
