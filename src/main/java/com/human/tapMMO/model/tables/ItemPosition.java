package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_positions")
public class ItemPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "item_id", updatable = false, nullable = false, unique = true)
    private Long itemId;

    @Column(name = "x", nullable = false, updatable = false)
    private int x;
    @Column(name = "y", nullable = false, updatable = false)
    private int y;
}
