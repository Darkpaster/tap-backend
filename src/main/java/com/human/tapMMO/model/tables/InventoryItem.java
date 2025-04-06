package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "item_id", updatable = false, nullable = false, unique = true)
    private Long itemId;

    @Column(name = "character_id", updatable = false, nullable = false)
    private Long characterId;

    @Column(name = "inventory_slot", nullable = false)
    private short inventorySlot;
}
