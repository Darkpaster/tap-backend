package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, updatable = false)
    private String name;
    @Column(name = "equip_slot", updatable = false)
    private byte equipSlot;
}
