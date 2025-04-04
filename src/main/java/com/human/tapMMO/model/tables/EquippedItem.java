package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "equipped_items")
public class EquippedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "item_id", updatable = false, nullable = false, unique = true)
    private Long itemId;

    @Column(name = "slot", nullable = false)
    private byte slot;
}
