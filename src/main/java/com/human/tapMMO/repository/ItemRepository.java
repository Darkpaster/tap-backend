package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
